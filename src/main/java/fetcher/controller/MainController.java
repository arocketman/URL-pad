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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.util.Callback;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    Clipboard clipboard;
    @FXML
    private ListView<PageEntry> listURL;
    final ObservableList<PageEntry> listItems = FXCollections.observableArrayList();

    public MainController(){
        clipboard = Clipboard.getSystemClipboard();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HandleClipboardChange listener = new HandleClipboardChange();
        Timeline repeatTask = new Timeline(new KeyFrame(Duration.millis(200), listener));
        repeatTask.setCycleCount(Timeline.INDEFINITE);
        repeatTask.play();
        listURL.setCellFactory(new Callback<ListView<PageEntry>, ListCell<PageEntry>>() {
            @Override
            public ListCell<PageEntry> call(ListView<PageEntry> param) {
                return new EntryCell();
            }
        });
        listURL.setItems(listItems);
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


class HandleClipboardChange implements EventHandler<ActionEvent> {

    String currentString;

    public HandleClipboardChange() {
        if (clipboard.hasString()) currentString = clipboard.getString();
        else currentString = "";
    }

    @Override
        public void handle(ActionEvent event) {
            String clipBoardStatus = clipboard.getString();
            if (clipboard.hasString() && !clipBoardStatus.equals(currentString) && Utils.isValidURL(clipBoardStatus)) {
                this.currentString = clipBoardStatus;
                PageEntry entry = new PageEntry(clipBoardStatus,MainController.this);
            }
        }
    }

}
