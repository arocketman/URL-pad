package fetcher;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
        primaryStage.setTitle("URL pad");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        }

    public static void main(String[] args) {
        launch(args);
    }




}
