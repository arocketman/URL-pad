package fetcher.controller;

import fetcher.model.PageEntry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class EntryCell extends ListCell<PageEntry> {
    @FXML
    HBox hbox;
    @FXML
    VBox vbox;
    @FXML
    Label title;
    @FXML
    Label description;
    @FXML
    ImageView imageView;

    /**
     * Constructor for EntryCell . The structure is a Horizontal box that has inside a picture and a vertical box. Inside the vertical box we have the title and the description.
     */
    public EntryCell(){
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/EntryCell.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * This is used to update a cell entry.
     * @param item the PageEntry that is being inserted / updated
     * @param empty
     */
    @Override
    protected void updateItem(PageEntry item, boolean empty) {
        super.updateItem(item, empty);
        if(empty){
            setGraphic(null);
            setText(null);
        }
        else{
            title.setText(item.getName());
            description.setText(item.getDescription());
            Image image;
            if(item.getPageSnapshot().isEmpty()){
                image = new Image("octopus.png" , 150 , 150, false , false );
            }
            else{
                image = new Image(item.getPageSnapshot(), 150 , 150, false , false );
            }
            imageView.setImage(image);
            setGraphic(hbox);
        }
    }
}
