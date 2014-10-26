package fetcher.model;

import fetcher.controller.MainController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class PageEntry {
    String URL;
    String Name;
    String Description;
    Date DateAdded;
    String pageSnapshot;
    MainController controller;

    public PageEntry(String URLstr,MainController controller){
        this.URL = URLstr;
        this.controller = controller;
        this.pageSnapshot = "";
        Thread workerThread = new Thread(new Worker());
        workerThread.start();
    }

    private void loadPage(){
        Document doc = null;
        try {
            doc = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.Name = doc.title();
        this.DateAdded = Calendar.getInstance().getTime();
        //Getting description
        Description = doc.select("p").first().text();
        //Getting the images , gets the first valid image (check isValidUrl function in utils class).
        Elements images = doc.select("img");
        //TODO: MAKE THE USER DECIDE WHICH IMAGE?
        if(!images.isEmpty()){
            pageSnapshot = "octopus.png";
            for(Element image : images){
                if(Utils.isValidURL(image.attr("src"))){
                    pageSnapshot = image.attr("src");
                    break;
                }

            }
        }
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

    class Worker implements Runnable{

        @Override
        public void run() {
            loadPage();
            controller.notifyControllerNewEntry(PageEntry.this);
        }
    }
}

