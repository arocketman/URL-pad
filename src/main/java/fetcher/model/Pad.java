package fetcher.model;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import fetcher.controller.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Abstracts the pad itself. Provides methods for handling the pad, such as saving and loading.
 * It also provides the two Observable lists that are observed by the Controller ListViews (one for the tags, one for the entries).
 */
public class Pad {

    private String padName;
    public ObservableList<PageEntry> listItems;
    public ObservableList<String> allTags;

    private ArrayList<String> resourcesToDelete;

    public Pad(String padName){
        this.padName = padName;
        if(!this.padName.endsWith(".json"))
            this.padName += ".json";

        listItems = FXCollections.observableArrayList();
        allTags = FXCollections.observableArrayList();
        resourcesToDelete = new ArrayList<String>();
    }

    /**
     * Checks if there's a duplicate entry (same url).
     * @param clipBoardStatus the URL copied inside the clipboard.
     * @return true if already in the list.
     */
    public boolean EntryAlreadyExists(String clipBoardStatus) {
        for(PageEntry entry : listItems){
            if(entry.getURL().equalsIgnoreCase(clipBoardStatus)) return true;
        }
        return false;
    }

    /**
     * Adds a tag to the array allTags only if it is not a duplicate.
     * @param tag tag to be added.
     */
    public void addTag(String tag){
        if(!Utils.exists(this.allTags,tag)){
            this.allTags.add(tag);
        }
    }

    /**
     * Saves the list of entries onto a json file.
     */
    public void savePad() {
        //TODO: Maybe this method should be called by a thread? The program should not close itself if the thread is still active. (NEEDS testing with lot of entries).
        JsonArray arrayOfEntries = new JsonArray();
        for (PageEntry entry : listItems) {
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
        deleteUnusedImages();
    }

    /**
     * Deletes all the images within the resourcesToDelete array.
     * Once an entry is deleted, the image associated will be deleted with this method as soon as the user hits the save button.
     */
    public void deleteUnusedImages(){
        //Deleting images associated with deleted pictures.
        for (String path : resourcesToDelete) {
            if(path.startsWith("file:///")){
                path = path.replace("file:///","");
            }
            Path pathToFile = FileSystems.getDefault().getPath(path);
            try {
                Files.delete(pathToFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        resourcesToDelete.clear();
    }

    /***
     * Loads the json file specified by LOCATIONS_FILESAVE onto the list provided by the user.
     * @param controller the MainController, this is used in the class "Worker" in PageEntry, in order to update the list without blocking the program.
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
                    addTag(tag);
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
     * Exports json file and images directory as a ZIP file.
     */
    public void exportPad() {
        final int BUFFER = 2048;
        byte data[] = new byte[BUFFER];
        
        File jsonFile = new File(padName);
        File imageDirectory = new File((new File(padName)).getParent() + "//urlPadImages"); 
        List<File> files = Utils.filesList(jsonFile, imageDirectory);
                
        try {
            FileOutputStream fileExport = new FileOutputStream(padName.replace(".json", ".zip"));
            ZipOutputStream zipExport = new ZipOutputStream(fileExport);
            
            for (File file : files) {
                System.out.println("Adding " + file.getName() + " to " + padName.replace(".json", ".zip"));
                FileInputStream input = new FileInputStream(file);
                zipExport.putNextEntry(new ZipEntry(file.getAbsolutePath()));
                
                int count;
                BufferedInputStream origin = new BufferedInputStream(input, BUFFER);
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    zipExport.write(data, 0, count);
                }
                origin.close();
            }
            zipExport.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Returns a filtered list based on a tag.
     * @param tagIndex the index of the tag.
     * @return the Filtered list or listItems if the tag is 'all'.
     */
    public ObservableList<PageEntry> getFilterTag(int tagIndex) {
        //Getting the tag.
        String filterTag = allTags.get(tagIndex);
        //Creating the new Observable list based on that value.
        if(filterTag.equals("all"))
            return listItems;
        else {
            ObservableList<PageEntry> entriesWithTag = FXCollections.observableArrayList();
            for (PageEntry entry : listItems) {
                if (entry.getTags().contains(filterTag)) entriesWithTag.add(entry);
            }
            return entriesWithTag;
        }
    }

    /**
     * Deletes an entry from the pad. The parameter filterList exists because the user might want to delete the entry within a filtered list.
     * @param index of the entry.
     * @param filterList the list whitin the user clicked delete from.
     */
    public void deleteEntry(int index , ObservableList<PageEntry> filterList) {
        if (filterList != listItems) {
            //If we are here, it means the user is trying to delete an entry from a filtered list, we need to find the index in the "all" list.
            for (int i = 0; i < listItems.size(); i++) {
                if (filterList.get(index).equals(listItems.get(i))) {
                    filterList.remove(index);
                    index = i;
                    break;
                }
            }
        }
        PageEntry tempEntry = listItems.get(index);
        //Adding the image of the deleted entry to the array of resources to be deleted. Such image will be deleted upon saving.
        resourcesToDelete.add(tempEntry.getPageSnapshot());
        listItems.remove(index);
    }

    public String getpadName() {
        return padName;
    }
}
