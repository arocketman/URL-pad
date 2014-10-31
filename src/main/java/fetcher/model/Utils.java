package fetcher.model;

import com.google.gson.*;
import fetcher.controller.MainController;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {
    private static final String LOCATIONS_FILESAVE = "pad.json";

    /**
     * Opens up a connection with the given URL and returns true if the response code is 200 (success)
     * @param URL_String the URL to test
     * @return Host reacability
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
        //TODO: Maybe this method should be called by a thread? The program should not close itself if the thread is still active.
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
     * @param list the list where the content of the json file will be saved.
     * @param controller the maincontroller, this is used in the class "Worker" in PageEntry, in order to update the list without blocking the program.
     */
    public static void loadPad(ObservableList<PageEntry> list , MainController controller){
        JsonParser parser = new JsonParser();
        Reader reader = null;
        try {
            reader = new FileReader(LOCATIONS_FILESAVE);
            JsonArray arrayOfEntries = parser.parse(reader).getAsJsonArray();
            for(JsonElement JsonElementEntry : arrayOfEntries){
                JsonObject JsonObjectEntry = JsonElementEntry.getAsJsonObject();
                PageEntry entry = new PageEntry(JsonObjectEntry.get("URL").getAsString(),controller);
                entry.setDescription(JsonObjectEntry.get("Description").getAsString());
                entry.setName(JsonObjectEntry.get("Name").getAsString());
                entry.setDateAdded(new SimpleDateFormat("dd MMMM yy , hh:mm:ss" , Locale.getDefault()).parse(JsonObjectEntry.get("DateAdded").getAsString()));
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
}
