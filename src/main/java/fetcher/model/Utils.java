/**
 *
 * File name : Utils.java
 *
 * The utils class is composed of static methods that are used all throughout the program.
 * There are both generic utilities methods such as checking if an element is inside an array and specific methods such as saving the pad.
 *
 */

package fetcher.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import fetcher.controller.MainController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Utils {
    private static final String LOCATIONS_FILESAVE = "pad.json";

    /**
     * Opens up a connection with the given URL and returns true if the response code is 200 (success)
     * @param URL_String the URL to test
     * @return Host reachability
     */
    public static boolean isValidURL(String URL_String) {
        try {
            URL url = new URL(URL_String);
            url.toURI();
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the image is a direct link one or not.
     * @param image the string to be checked.
     * @return true if the string is a direct link image.
     */
    public static boolean isDirectLinkImage(String image){
        return image.endsWith(".jpg") || image.endsWith(".png") || image.endsWith(".bmp");
    }

    /**
     * Saves the list of entries onto a json file.
     * @param list the list of PageEntry to be saved
     */
    public static void savePad(ObservableList<PageEntry> list){
        //TODO: Maybe this method should be called by a thread? The program should not close itself if the thread is still active. (NEEDS testing with lot of entries).
        JsonArray arrayOfEntries = new JsonArray();
        for(PageEntry entry : list){
            JsonObject singleEntry = entry.saveEntry();
            arrayOfEntries.add(singleEntry);
        }

        Gson gson = new Gson();
        try {
            Writer writer = new FileWriter(LOCATIONS_FILESAVE);
            gson.toJson(arrayOfEntries, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /***
     * Loads the json file specified by LOCATIONS_FILESAVE onto the list provided by the user.
     * @param controller the maincontroller, this is used in the class "Worker" in PageEntry, in order to update the list without blocking the program.
     */
    public static void loadPad(MainController controller){
        JsonParser parser = new JsonParser();
        try {
            Reader reader = new FileReader(LOCATIONS_FILESAVE);
            JsonArray arrayOfEntries = parser.parse(reader).getAsJsonArray();
            for(JsonElement JsonElementEntry : arrayOfEntries){
                JsonObject JsonObjectEntry = JsonElementEntry.getAsJsonObject();
                PageEntry entry = new PageEntry(controller);
                entry.setURL(JsonObjectEntry.get("URL").getAsString());
                entry.setDescription(JsonObjectEntry.get("Description").getAsString());
                entry.setName(JsonObjectEntry.get("Name").getAsString());
                entry.setPageSnapshot(JsonObjectEntry.get("pageSnapshot").getAsString());
                entry.setDateAdded(new SimpleDateFormat("dd MMMM yy , hh:mm:ss" , Locale.getDefault()).parse(JsonObjectEntry.get("DateAdded").getAsString()));
                HashSet<String> tags = new Gson().fromJson(JsonObjectEntry.get("Tags"), new TypeToken<Set<String>>(){}.getType());
                for(String tag : tags)
                    controller.addTag(tag);
                entry.setTags(tags);
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes sure that the file LOCATIONS_FILESAVE exists.
     * @return true if the file exists.
     */
    public static boolean savedPadExists() {
        return (new File(LOCATIONS_FILESAVE).exists());
    }

    /**
     * Checks if an element exists in a given array.
     * @param array the array to search within
     * @param element the element we want to search for
     * @param <T> the generic type.
     * @return true if the given element exists in the array.
     */
    public static <T> boolean exists(List<T> array , T element){
        for(T currentElement : array){
            if(element.equals(currentElement)) return true;
        }
        return false;
    }

    public static <T> ObservableList<T> convertToObservableList(HashSet<T> set) {
        ObservableList<T> convertedList = FXCollections.observableArrayList();
        for(T elem : set){
            convertedList.add(elem);
        }
        return convertedList;
    }

    /**
     * Converts a given URL to an image which will be saved on /images/ folder.
     * It uses a WebView which basically renders the page in the browser. Once the rendering is complete the screenshot is taken.
     * @param url Url to be converted to image.
     * @return the file location.
     */
    public static String getWebsiteSnapshot(final String url){
        //Creating the window and initializing the browser.
        final Stage urlStage = new Stage();
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        final Rectangle2D screensize = Screen.getPrimary().getVisualBounds();
        urlStage.setWidth(screensize.getWidth());
        urlStage.setHeight(screensize.getHeight());
        VBox.setVgrow(browser, Priority.ALWAYS);
        Scene scene = new Scene(new Group());
        VBox root = new VBox();
        //Retrieving the url string and sanitizing it for it to be saved as a File, it removes the http:// and www. from the url as well.
        String tempName = url.replaceAll("(http://|https://|http://www\\.|www\\.)","").replaceAll("[^a-zA-Z0-9.-]", "_");
        if(tempName.length() > 10) {
            tempName.substring(0, 10);
        }
        tempName += ".png";
        final String fileName = tempName;
        //Loading the url and adding a listener to the changed state property (We want to learn when the page loads).
        webEngine.load(url);
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                if (newValue.equals(Worker.State.SUCCEEDED)) {
                    //If the page loads, let's take a snapshot of it.
                    WritableImage snapshot = new WritableImage((int)screensize.getWidth(), (int)screensize.getHeight());
                    browser.snapshot(null, snapshot);
                    //Save the snapshot on the hard drive.
                    File imgDir = (new File("urlpadimages"));
                    imgDir.mkdir();
                    File file = new File(imgDir.getAbsolutePath() + "\\" + fileName);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot, null);
                    try {
                        ImageIO.write(renderedImage, "png", file);
                        urlStage.close();
                    } catch (IOException ex) {
                        //TODO: What to do here?
                        ex.printStackTrace();
                    }
                }
                else if(newValue.equals(Worker.State.FAILED)){
                    //TODO: Send an error message somehow.
                    urlStage.close();
                }
            }
        });

        root.getChildren().addAll(browser);
        scene.setRoot(root);
        urlStage.setIconified(true);
        urlStage.setScene(scene);
        urlStage.show();

        return fileName;
    }
}

