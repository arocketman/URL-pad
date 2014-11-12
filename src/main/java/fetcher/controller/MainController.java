/**
 *
 * File name : MainController.java
 *
 * This class is the controller for the Main.fxml file.
 * Its main purpose is to handle correctly the ListView containing the urls.
 * It also handles the menu bar and the upper buttons.
 *
 */

package fetcher.controller;

import fetcher.model.EntriesComparators;
import fetcher.model.Utils;
import fetcher.model.PageEntry;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import java.util.Comparator;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    Clipboard clipboard;
    public final ObservableList<PageEntry> listItems = FXCollections.observableArrayList();
    //TODO: Make this an observable list, so that I can handle them like a list
    public final ObservableList<String> allTags = FXCollections.observableArrayList();

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
        listURL.setItems(listItems);
        tagsListView.setItems(allTags);
        setupButtons();
        //Loading the pad if there's a saved one.
        if(Utils.savedPadExists())Utils.loadPad(this);
        tagsListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListenerTagsFilter());
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
                if(allTags.size() == 0) allTags.add("all");
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

    @FXML
    private void handleExitMenuButton(){
        Platform.exit();
    }

    @FXML
    private void handleSaveMenuButton(){
        Utils.savePad(listItems);
    }

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
        ObservableList<PageEntry> filterList = listURL.getItems();
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
        listItems.remove(index);
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
                Collections.sort(listItems, EntriesComparators.getDateComparator());
            else if(buttonPressed.getId().equalsIgnoreCase("dateDESC")){
                Collections.sort(listItems, EntriesComparators.getDateComparator().reversed());
            }
            else if(buttonPressed.getId().equalsIgnoreCase("alphASC"))
                Collections.sort(listItems, EntriesComparators.getAlphabeticalComparator());
            else if(buttonPressed.getId().equalsIgnoreCase("alphDESC")){
                Collections.sort(listItems, EntriesComparators.getAlphabeticalComparator().reversed());
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
                if (!EntryAlreadyExists(clipBoardStatus)) {
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
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            //Getting the tag.
            String filterTag = allTags.get(newValue.intValue());
            //Creating the new Observable list based on that value.
            ObservableList<PageEntry> entriesWithTag = FXCollections.observableArrayList();
            if(filterTag.equals("all"))
                listURL.setItems(listItems);
            else {
                for (PageEntry entry : listItems) {
                        if (entry.getTags().contains(filterTag)) entriesWithTag.add(entry);
                }
                listURL.setItems(entriesWithTag);
            }
        }
    }



}
