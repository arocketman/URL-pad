package fetcher.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 *
 * File name : Utils.java
 *
 * The utils class is composed of static methods that are used all throughout the program.
 * There are both generic utilities methods such as checking if an element is inside an array and specific methods such as saving the pad.
 *
 */
public class Utils {

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

    /**
     * Converts a HashSet to an observable list .
     * @param set The set to be converted
     * @param <T> the generic type.
     * @return an ObservableList with the Hashset values.
     */
    public static <T> ObservableList<T> convertToObservableList(HashSet<T> set) {
        ObservableList<T> convertedList = FXCollections.observableArrayList();
        for(T elem : set){
            convertedList.add(elem);
        }
        return convertedList;
    }

    public static boolean FileExists(String fileName){
            return (new File(fileName).exists());
    }

    /**
     * Builds a window popup showing a message.
     * @param message the message to be shown.
     */
    public static void createAlertMessage(String message){
        final Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        Button ok = new Button("OK");
        final Label label = new Label(message);
        vBox.getChildren().addAll(label,ok);
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialogStage.close();
            }
        });
        dialogStage.setScene(new Scene(vBox));
        dialogStage.show();
    }

    public static String saveImageFromURL(String URL , String directory){
        BufferedImage image;
        try {
            image = ImageIO.read(new URL(URL));
            File imageFile = new File(directory + "\\" +UUID.randomUUID().toString() + ".jpg");
            ImageIO.write(image,"png",imageFile);
            return imageFile.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

