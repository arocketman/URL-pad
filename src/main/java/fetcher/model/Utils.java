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

    public static boolean isDirectLinkImage(String image){
        return image.endsWith(".jpg") || image.endsWith(".png") || image.endsWith(".bmp");
    }

    public static void savePad(ObservableList<PageEntry> list){
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
                entry.setPageSnapshot(JsonObjectEntry.get("pageSnapshot").getAsString());
                entry.setDateAdded(new SimpleDateFormat("dd MMMM yy , hh:mm:ss" , Locale.getDefault()).parse(JsonObjectEntry.get("DateAdded").getAsString()));
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static boolean savedPadExists() {
        return (new File(LOCATIONS_FILESAVE).exists());
    }
}
