package gui;

import gui.Listener.OnCloseListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Starter extends Application{

    private List<OnCloseListener> closeListeners = new ArrayList<OnCloseListener>();

    public void start(Stage stage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_MODENA);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/startpage.fxml"));
        Parent root = loader.load();
        closeListeners.add(loader.getController());
        Scene scene = new Scene(root, 800, 600);
        //scene.getStylesheets().add(getClass().getResource("/css/modena_dark.css").toExternalForm());
        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> close());
        stage.show();
    }

    public void close() {
        closeListeners.forEach(OnCloseListener::onClose);
        Platform.exit();
        System.exit(0);
    }
}
