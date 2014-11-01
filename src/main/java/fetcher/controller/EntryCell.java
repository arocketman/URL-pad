package fetcher.controller;

import fetcher.model.PageEntry;
import fetcher.model.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class EntryCell extends ListCell<PageEntry> {
    PageEntry entry;
    MainController controller;

    @FXML
    HBox hbox;
    @FXML
    Label title;
    @FXML
    Label description;
    @FXML
    ImageView imageView;
    @FXML
    Label date;
    @FXML
    Label urllbl;
    @FXML
    Label tags;

    /**
     * Constructor for EntryCell . The structure is a Horizontal box that has inside a picture and a vertical box. Inside the vertical box we have the title and the description.
     */
    public EntryCell(MainController controller){
        super();
        this.controller = controller;
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
            this.entry = item;
            date.setText("Added on : " + item.getDateAdded().toString());
            title.setText(item.getName());
            urllbl.setText(item.getURL());
            description.setText(item.getDescription());
            Image image;
            if(item.getPageSnapshot().isEmpty()){
                image = new Image("images/octopus.png", 150 , 150, false , false );
            }
            else{
                image = new Image(item.getPageSnapshot(), 150 , 150, false , false );
            }
            imageView.setImage(image);
            updateTags();
            setGraphic(hbox);
            this.setOnMouseClicked(new mouseClickedHandler());
        }
    }

    /**
     * Updates the UI with all the new added tags.
     */
    public void updateTags(){
        this.tags.setText("");
        for(String tag : entry.getTags()) {
            if (tags.getText().isEmpty())
                tags.setText(tag);
            else
                tags.setText(tags.getText() + "," + tag);
        }
    }

    class mouseClickedHandler implements EventHandler<MouseEvent>{

        /**
         * Opens up the default browser to open the url of the selected entry. Only works if double-clicked.
         * @param event
         */
        @Override
        public void handle(MouseEvent event) {
            //Double left click : Open URL.
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(urllbl.getText()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        //TODO: Tell the user the URL is probably wrong.
                        e.printStackTrace();
                    }
            }
            //Right click: Add tag to selected item.
            else if(event.getButton() == MouseButton.SECONDARY){
                final Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.WINDOW_MODAL);
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                Button ok = new Button("OK");
                final TextField textfield = new TextField();
                vBox.getChildren().addAll(new Label("Tag:"),textfield,ok);
                //Handler of the OK pressing button. Gets the tag and adds it to the existing ones if it doesn't already exists.
                ok.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String text = textfield.getText();
                        if(!text.isEmpty()) {
                            //Making sure the tag doesn't already exists.
                            if(!entry.getTags().contains(text)) {
                                entry.addTag(text);
                                updateTags();
                                if(!Utils.exists(controller.allTags,text)){
                                    controller.allTags.add(text);
                                }
                            }
                            dialogStage.close();
                        }
                    }
                });
                dialogStage.setScene(new Scene(vBox));
                dialogStage.show();
            }
        }
    }

}
