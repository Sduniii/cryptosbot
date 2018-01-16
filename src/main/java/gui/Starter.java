package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Starter extends Application{
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/startpage.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
    }
}
