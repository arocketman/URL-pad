package fetcher.model;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class provides methods to take a snapshot of a given URL.
 * It functions through the WebView and WebEngine provided by Javafx.
 */
public class Snapshotter {

    Stage urlStage;
    WebView browser;
    WebEngine webEngine;
    Rectangle2D screenSize;
    String url;

    public Snapshotter(String url){
        this.url = url;
        urlStage = new Stage();
        browser = new WebView();
        webEngine = browser.getEngine();
        screenSize = Screen.getPrimary().getVisualBounds();
        setupUI();
    }

    /**
     * Takes care of the graphical aspects of the browser.
     */
    private void setupUI() {
        //Creating the window and initializing the browser.
        urlStage.setWidth(screenSize.getWidth());
        urlStage.setHeight(screenSize.getHeight());
        VBox.setVgrow(browser, Priority.ALWAYS);
        Scene scene = new Scene(new Group());
        VBox root = new VBox();

        root.getChildren().addAll(browser);
        scene.setRoot(root);
        urlStage.setIconified(true);
        urlStage.setScene(scene);
        urlStage.show();
    }

    /**
     * Converts a given URL to an image which will be saved on /images/ folder.
     * It uses a WebView which basically renders the page in the browser. Once the rendering is complete the screenshot is taken.
     * @return the file location.
     */
    public String getWebsiteSnapshot(){
        String fileName = buildFileNameFromUrl();
        webEngine.load(url);
        webEngine.getLoadWorker().stateProperty().addListener(new BrowserStatusChangeListener(fileName));
        //Timeout for loading pages. If the page is still loading after 20 seconds we force it to stop.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (webEngine.getLoadWorker().getState() == Worker.State.RUNNING) {
                            webEngine.load(null);
                            urlStage.close();
                        }
                    }
                });

            }
        },30000);

        return fileName;
    }

    /**
     * Given a BufferedImage it saves it under the specified folder.
     * @param destinationFolder Name of the destination folder where the image will be stored.
     * @param fileName Name of the file to be saved.
     * @param renderedImage the image to be saved.
     * @return true if the save has success.
     */
    public static boolean saveImage(String destinationFolder , String fileName , BufferedImage renderedImage){
        //Save the snapshot on the hard drive.
        File imgDir = (new File(destinationFolder));
        imgDir.mkdir();
        File file = new File(imgDir.getAbsolutePath() + "\\" + fileName);
        try {
            ImageIO.write(renderedImage, "png", file);
            return true;
        } catch (IOException ex) {
            //TODO: What to do here?
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Creates the file name of the image based on the url. It sanitizes the input using regex so that no special characters are used.
     * @return the fileName built up from the url.
     */
    private String buildFileNameFromUrl(){
        //Retrieving the url string and sanitizing it for it to be saved as a File, it removes the http:// and www. from the url as well.
        String tempName = url.replaceAll("(http://|https://|http://www\\.|www\\.)","").replaceAll("[^a-zA-Z0-9.-]", "_");
        if(tempName.length() > 10) {
            //TODO: Consider grabbing the last part to distinguish the urls. Or just make something up like first 5 letters.. a dash and the last five letters.
            tempName = tempName.substring(0, 10);
        }
        tempName += ".png";
        return tempName;
    }

    /**
     * This class listens for browser's state change. Checks if the page has finished loading, and calls for the saveImage method.
     */
    class BrowserStatusChangeListener implements ChangeListener<Worker.State> {

        String fileName;

        public BrowserStatusChangeListener(String name){
            this.fileName = name;
        }

        @Override
        public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
            if (newValue.equals(Worker.State.SUCCEEDED)) {
                if(webEngine.getDocument().getBaseURI().equals("about:blank")) return;
                //If the page loads, let's take a snapshot of it.
                WritableImage snapshot = new WritableImage((int) screenSize.getWidth(), (int) screenSize.getHeight());
                browser.snapshot(null, snapshot);
                BufferedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
                //Some math to get a little better thumbnail. Starting x is at 1/4 of the totale page. Goes for a width of half of the screen size to attempt and get the 'core' of the content.
                renderedImage = renderedImage.getSubimage((int)(screenSize.getWidth() /4),0,(int)( screenSize.getWidth()  / 2 ),(int) screenSize.getHeight() / 2);
                saveImage("urlpadimages",fileName,renderedImage);
                urlStage.close();
                webEngine.load(null);
            }
            else if(newValue.equals(Worker.State.FAILED)){
                //TODO: Send an error message somehow.
                urlStage.close();
            }
        }

    }

}




