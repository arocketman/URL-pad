/**
 *
 * File name : PageEntry.java
 *
 * This class represents the abstraction for a single URL entry.
 * Using Jsoup it fetches all the needed informations from the web page.
 *
 */

package fetcher.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fetcher.controller.MainController;
import javafx.application.Platform;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PageEntry {
    String URL;
    String Name;
    String Description;
    Date DateAdded;
    String pageSnapshot;
    MainController controller;
    HashSet<String> tags;

    public PageEntry(String URLstr,MainController controller){
        this.URL = URLstr;
        this.controller = controller;
        this.pageSnapshot = "images/octopus.png";
        this.Description = "";
        this.tags = new HashSet<String>();
        //TODO: This is to 'reset' the filter by tags (Show all entries) . Probably needs a better solution.
        tags.add("all");
        Thread workerThread = new Thread(new Worker(true));
        workerThread.start();
    }

    /**
     * Constructor used just when loading from the json file.
     * @param controller
     */
    public PageEntry(MainController controller){
        this.controller = controller;
        this.tags = new HashSet<String>();
        tags.add("all");
        Thread workerThread = new Thread(new Worker(false));
        workerThread.start();
    }

    /**
     * Loads the page provided by the URL parameter and uses Jsoup to retrieve its content.
     */
    private void loadPage(){
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (HttpStatusException e){
            System.out.println("Failed to load page : " + URL + " , HTTP status code received was : " + e.getStatusCode());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        this.Name = doc.title();
        this.DateAdded = Calendar.getInstance().getTime();
        //Getting description
        if(doc.select("p").first() != null)
            Description = doc.select("p").first().text();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pageSnapshot = "file:///" + (new File("")).getAbsolutePath() + "\\urlpadimages\\" + Utils.getWebsiteSnapshot(getURL());

            }
        });
    }

    /**
     * Creates a JsonObject containing a json representation of this PageEntry.
     * @return savedJson is the JsonObject containing the fields of this PageEntry.
     */
    public JsonObject saveEntry(){
        JsonObject savedJson = new JsonObject();
        JsonArray savedTags = new JsonArray();
        savedJson.addProperty("URL",URL);
        savedJson.addProperty("Name",Name);
        savedJson.addProperty("Description",Description);
        savedJson.addProperty("DateAdded", new SimpleDateFormat("dd MMMM yy , hh:mm:ss" , Locale.getDefault()).format(this.DateAdded));
        savedJson.addProperty("pageSnapshot",pageSnapshot);
        for(String tag : tags)
            savedTags.add(new JsonPrimitive(tag));
        savedJson.add("Tags",savedTags);
        return savedJson;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Date getDateAdded() {
        return DateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        DateAdded = dateAdded;
    }

    public String getPageSnapshot() {
        return pageSnapshot;
    }

    public void setPageSnapshot(String pageSnapshot) {
        this.pageSnapshot = pageSnapshot;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void addTag(String tag){
        this.tags.add(tag);
    }

    public HashSet<String> getTags() {
        return tags;
    }

    public void setTags(HashSet tags) {
        this.tags = tags;
    }

    public void clearTags() {
        tags.clear();
        tags.add("all");
    }

    public void deleteTag(String selectedItem) {
        if(tags.contains(selectedItem) && !selectedItem.equalsIgnoreCase("all")) tags.remove(selectedItem);
    }

    class Worker implements Runnable{

        boolean loadFromURL;

        public Worker(boolean loadFromUrl){
            this.loadFromURL = loadFromUrl;
        }

        @Override
        public void run() {
            if(loadFromURL)loadPage();
            controller.notifyControllerNewEntry(PageEntry.this);
        }
    }
}

