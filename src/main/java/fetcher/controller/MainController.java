package fetcher.controller;

import fetcher.model.EntriesComparators;
import fetcher.model.Pad;
import fetcher.model.Utils;
import fetcher.model.PageEntry;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Duration;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;


/**
 *
 * File name : MainController.java
 *
 * This class is the controller for the Main.fxml file.
 * Its main purpose is to handle correctly the ListView containing the urls.
 * It also handles the menu bar and the upper buttons.
 *
 */
public class MainController implements Initializable {

    Clipboard clipboard;
    public Pad pad;

    @FXML
    private ListView<PageEntry> listURL;
    @FXML
    private Button saveBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private ListView<String> tagsListView;

    public MainController(){
        clipboard = Clipboard.getSystemClipboard();
    }

    public void setPadName(String padName){
        pad = new Pad(padName);
        listURL.setItems(pad.listItems);
        tagsListView.setItems(pad.allTags);
        //Loading the pad if there's a saved one.
        if(Utils.FileExists(pad.getpadName()))pad.loadPad(this);

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
                return new EntryCell(MainController.this);

            }
        });

        setupButtons();
        tagsListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListenerTagsFilter());
    }

    /**
     * Updates the listView with the Entry which was processed by a worker thread from PageEntry class.
     * @param entry the entry to be added to the listview.
     */
    public void notifyControllerNewEntry(final PageEntry entry){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pad.listItems.add(entry);
                //TODO: Doesn't need to be here, probably can be put in the add method itself.
                if(pad.allTags.size() == 0) pad.allTags.add("all");
         }});
    }

    /**
     * Refreshes the list-view setting the listURL to null and then back to the previous status.
     */
    public void refreshListView(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ObservableList<PageEntry> entries = listURL.getItems();
                listURL.setItems(null);
                listURL.setItems(entries);
            }
        });
    }

    /**
     * Takes care of setting up the buttons (in the upper bar) UI and listeners.
     */
    public void setupButtons(){
        //TODO: I think I can make this whole section a little cleaner by using CSS.
        //Setting up the buttons
        Image imageDelete = new Image(getClass().getResourceAsStream("/images/delete.png"));
        deleteBtn.setGraphic(new ImageView(imageDelete));
        deleteBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleDeleteMenuButton();
            }
        });
        deleteBtn.setText("Delete");
        deleteBtn.setContentDisplay(ContentDisplay.TOP);
        //Button save
        Image imageSave = new Image(getClass().getResourceAsStream("/images/save.png"));
        saveBtn.setGraphic(new ImageView(imageSave));
        saveBtn.setText("Save");
        saveBtn.setContentDisplay(ContentDisplay.TOP);
        saveBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleSaveMenuButton();
            }
        });
    }

    /**
     * Exits the program.
     */
    @FXML
    private void handleExitMenuButton(){
        Platform.exit();
    }

    /**
     * Saves the pad.
     */
    @FXML
    private void handleSaveMenuButton(){
        pad.savePad();
    }

    /**
     * Clear the tags from the selected entry.
     */
    @FXML
    private void handleClearTagsMenuButton(){
        if (listURL.getSelectionModel().getSelectedItem() == null) return;
        listURL.getSelectionModel().getSelectedItem().clearTags();
        }

    /**
     * This method deletes an entry from the listView.
     */
    @FXML
    private void handleDeleteMenuButton(){
        if (listURL.getSelectionModel().getSelectedItem() == null) return;
        int index = listURL.getSelectionModel().getSelectedIndex();
        pad.deleteEntry(index,listURL.getItems());
    }

    /**
     * Handles the menu sorting. Checks which MenuItem was clicked and sorts accordingly using the EntrisComparators class.
     * @param actionEvent
     */
    @FXML
    public void handleSortMenuButton(ActionEvent actionEvent) {
        Object eventTriggerer = actionEvent.getSource();
        if(eventTriggerer instanceof MenuItem){
            MenuItem buttonPressed = (MenuItem) eventTriggerer;
            if(buttonPressed.getId().equalsIgnoreCase("dateASC"))
                Collections.sort(pad.listItems, EntriesComparators.getDateComparator());
            else if(buttonPressed.getId().equalsIgnoreCase("dateDESC")){
                Collections.sort(pad.listItems, EntriesComparators.getDateComparator().reversed());
            }
            else if(buttonPressed.getId().equalsIgnoreCase("alphASC"))
                Collections.sort(pad.listItems, EntriesComparators.getAlphabeticalComparator());
            else if(buttonPressed.getId().equalsIgnoreCase("alphDESC")){
                Collections.sort(pad.listItems, EntriesComparators.getAlphabeticalComparator().reversed());
            }
        }
    }

    class HandleClipboardChange implements EventHandler<ActionEvent> {

        String currentString;

        public HandleClipboardChange() {
            if (clipboard.hasString()) currentString = clipboard.getString();
            else currentString = "";
        }

        /**
         * Checks if there's any change in the clipboard. Makes sure the change is a URL, creates a PageEntry based on that URL.
         * @param event the mouse click event.
         */
        @Override
        public void handle(ActionEvent event) {
            String clipBoardStatus = clipboard.getString();
            if (clipboard.hasString() && !clipBoardStatus.equals(currentString) && Utils.isValidURL(clipBoardStatus)) {
                this.currentString = clipBoardStatus;
                if (!pad.EntryAlreadyExists(clipBoardStatus)) {
                    new PageEntry(clipBoardStatus, MainController.this);
                }
            }
        }

    }

    /**
     * Takes care of handling the filtering of objects when a different cell in  the listview of tags is selected.
     */
    class ChangeListenerTagsFilter implements ChangeListener<Number>{

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number tagNewIndex) {
            listURL.setItems(pad.getFilterTag(tagNewIndex.intValue()));
        }
    }



}
