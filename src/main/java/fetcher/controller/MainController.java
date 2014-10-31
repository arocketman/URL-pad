package fetcher.controller;

import fetcher.model.Utils;
import fetcher.model.PageEntry;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    Clipboard clipboard;
    final ObservableList<PageEntry> listItems = FXCollections.observableArrayList();

    @FXML
    private ListView<PageEntry> listURL;
    @FXML
    private Button saveBtn;
    @FXML
    private Button deleteBtn;


    public MainController(){
        clipboard = Clipboard.getSystemClipboard();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Setting up a periodic clipboard checker.
        HandleClipboardChange listener = new HandleClipboardChange();
        Timeline repeatTask = new Timeline(new KeyFrame(Duration.millis(200), listener));
        repeatTask.setCycleCount(Timeline.INDEFINITE);
        repeatTask.play();
        //Setting up the ListView
        listURL.setCellFactory(new Callback<ListView<PageEntry>, ListCell<PageEntry>>() {
            @Override
            public ListCell<PageEntry> call(ListView<PageEntry> param) {
                return new EntryCell();

            }
        });
        listURL.setItems(listItems);

        //TODO: I think I can make this whole section a little cleaner by using CSS.
        //Setting up the buttons
        Image imageDelete = new Image(getClass().getResourceAsStream("/delete.png"));
        deleteBtn.setGraphic(new ImageView(imageDelete));
        deleteBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(listItems.size() > 0) {
                    int index = listURL.getSelectionModel().getSelectedIndex();
                    listItems.remove(index);
                }
            }
        });
        deleteBtn.setText("Delete");
        deleteBtn.setContentDisplay(ContentDisplay.TOP);
        //Button save
        Image imageSave = new Image(getClass().getResourceAsStream("/save.png"));
        saveBtn.setGraphic(new ImageView(imageSave));
        saveBtn.setText("Save");
        saveBtn.setContentDisplay(ContentDisplay.TOP);
        saveBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Utils.savePad(listItems);
            }
        });

        //Loading the pad if there's a saved one.
        if(Utils.savedPadExists())Utils.loadPad(listItems,this);
    }

    /**
     * Updates the listView with the Entry which was processed by a worker thread from PageEntry class
     * @param entry the entry to be added to the listview.
     */
    public void notifyControllerNewEntry(final PageEntry entry){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listItems.add(entry);
    }
});

        }

    /**
     * Checks if there's a duplicate link
     * @param clipBoardStatus the URL copied inside the clipboard
     * @return true if already in the list
     */
    private boolean EntryAlreadyExists(String clipBoardStatus) {
        for(PageEntry entry : listItems){
            if(entry.getURL().equalsIgnoreCase(clipBoardStatus)) return true;
        }
        return false;
    }


    class HandleClipboardChange implements EventHandler<ActionEvent> {


        String currentString;

        public HandleClipboardChange() {
            if (clipboard.hasString()) currentString = clipboard.getString();
            else currentString = "";
        }


        /**
         * Checks if there's any change in the clipboard. Makes sure the change is a URL, creates a PageEntry based on that URL.
         * @param event
         */
        @Override
        public void handle(ActionEvent event) {
            String clipBoardStatus = clipboard.getString();
            if (clipboard.hasString() && !clipBoardStatus.equals(currentString) && Utils.isValidURL(clipBoardStatus)) {
                this.currentString = clipBoardStatus;
                if (!EntryAlreadyExists(clipBoardStatus)) {
                    PageEntry entry = new PageEntry(clipBoardStatus, MainController.this);
                }
            }
        }

    }



}
