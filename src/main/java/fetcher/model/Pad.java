package fetcher.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import fetcher.controller.MainController;
import javafx.collections.ObservableList;

import java.io.*;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstracts the pad itself.
 */
public class Pad {

    String padName;

    public Pad(String padName){
        this.padName = padName;
        if(!this.padName.endsWith(".json"))
            this.padName += ".json";
    }

    /**
     * Saves the list of entries onto a json file.
     * @param list the list of PageEntry to be saved
     */
    public void savePad(ObservableList<PageEntry> list){
        //TODO: Maybe this method should be called by a thread? The program should not close itself if the thread is still active. (NEEDS testing with lot of entries).
        JsonArray arrayOfEntries = new JsonArray();
        for(PageEntry entry : list){
            JsonObject singleEntry = entry.saveEntry();
            arrayOfEntries.add(singleEntry);
        }

        Gson gson = new Gson();
        try {
            Writer writer = new FileWriter(padName);
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
    public void loadPad(MainController controller){
        JsonParser parser = new JsonParser();
        try {
            Reader reader = new FileReader(padName);
            JsonArray arrayOfEntries = parser.parse(reader).getAsJsonArray();
            for(JsonElement JsonElementEntry : arrayOfEntries){
                JsonObject JsonObjectEntry = JsonElementEntry.getAsJsonObject();
                PageEntry entry = new PageEntry(controller);
                entry.loadEntry(JsonObjectEntry);
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
    public boolean savedPadExists() {
        return (new File(padName).exists());
    }
}
