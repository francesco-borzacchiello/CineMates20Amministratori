package it.unina.ingSw.cineMates20;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class FXMLUtils {

    public static Scene setRoot(String fxml) throws IOException {
        return new Scene(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("FXML/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
