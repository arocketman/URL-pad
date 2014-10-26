package fetcher.controller;

import fetcher.model.PageEntry;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class EntryCell extends ListCell<PageEntry> {
    HBox hbox;
    VBox vbox;
    Label title;
    Label description;
    ImageView imageView;

    public EntryCell(){
        super();
        hbox = new HBox();
        vbox = new VBox();
        vbox.setMinHeight(200);
        title = new Label();
        description = new Label();
        imageView = new ImageView();
        vbox.getChildren().addAll(title,description);
        hbox.getChildren().addAll(imageView,vbox);
    }

    @Override
    protected void updateItem(PageEntry item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty){
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
