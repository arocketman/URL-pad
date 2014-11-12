package fetcher.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
     * @param <T>
     * @return an ObservableList with the Hashset values.
     */
    public static <T> ObservableList<T> convertToObservableList(HashSet<T> set) {
        ObservableList<T> convertedList = FXCollections.observableArrayList();
        for(T elem : set){
            convertedList.add(elem);
        }
        return convertedList;
    }
}

