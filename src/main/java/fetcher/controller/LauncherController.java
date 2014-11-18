package fetcher.controller;

import fetcher.Main;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

/**
 * This class is the controller for Launcher.fxml. It takes care to handle the new/open pad operations.
 * Once the user picks one of the two options, the Launcher will provide to open a MainController instance and close its own.
 */
public class LauncherController {

    /**
     * Creates a box where the user can insert the pad name which will be created.
     * @param event the mouse-click event.
     */
    @FXML
    public void CreateNewPad(Event event) {
        Stage stage = getStageFromEvent(event);
        FileChooser fileChooser = buildFileChooser();
        File file = fileChooser.showSaveDialog(stage);
        if(file != null)
            startPadMain(file.getAbsolutePath(),stage);
    }

    /**
     * Has the task to open the MainController and close the launcher stage.
     * @param padName the padName to be passed to the MainController.
     * @param stage the launcher stage to be closed.
     */
    private void startPadMain(String padName, Stage stage){
        Stage primaryStage = new Stage(StageStyle.DECORATED);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/Main.fxml"));
        primaryStage.setTitle("URL pad");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon.png")));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainController controller = fxmlLoader.getController();
        controller.setPadName(padName);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        stage.close();
    }

    /**
     * Handler when the 'load pad' button is clicked. Opens up a FileChooser open dialog.
     * @param event the mouse-click event that called this method.
     */
    @FXML
    public void LoadPadButtonHandler(Event event) {
        FileChooser fileChooser = buildFileChooser();
        fileChooser.setTitle("Open Resource File");
        Stage stage = getStageFromEvent(event);
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            if(isValidPad(file))
                startPadMain(file.getAbsolutePath(),stage);
        }
    }

    /**
     * Verifies that the selected file is a correct URL pad generated json file.
     * @param file the selected file.
     * @return true if it is a valid pad.
     */
    public boolean isValidPad(File file){
        //TODO
        return true;
    }

    /**
     * Given an event, it returns its Stage.
     * In our case, we use this to retrieve the stage where the buttons where clicked.
     * @param event the event.
     * @return the Stage given an event.
     */
    private Stage getStageFromEvent(Event event){
        return (Stage)((Node)event.getSource()).getScene().getWindow();
    }

    /**
     * Builds a FileChooser with a json extension filter.
     * @return a FileChooser with a json extension filter.
     */
    private FileChooser buildFileChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser;
    }
}
