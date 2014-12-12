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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Closeable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    
    /**
     * Generates a zip file from a given directory.
     * @param directory the directory to zip.
     * @param zipfile the zip file to be generated.
     */
    public static void zip(File directory, File zipfile){
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        OutputStream out = null;
        try {
            out = new FileOutputStream(zipfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Closeable res = out;
        
        try {
            ZipOutputStream zipOut = new ZipOutputStream(out);
            res = zipOut;
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File file : directory.listFiles()) {
                    String name = base.relativize(file.toURI()).getPath();
                    if (file.isDirectory()) {
                        queue.push(file);
                        name = name.endsWith("/") ? name : name + "/";
                        zipOut.putNextEntry(new ZipEntry(name));
                    } else {
                        zipOut.putNextEntry(new ZipEntry(name));
                        copy(file, zipOut);
                        zipOut.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Controls the copying of data into zip file
     * @param file input from directory to be zipped.
     * @param out zipped output.
     */
    private static void copy(File file, OutputStream out){
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
          byte[] buffer = new byte[1024];
            while (true) {
                int readCount = in.read(buffer);
                if (readCount < 0) {
                  break;
                }
                out.write(buffer, 0, readCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

