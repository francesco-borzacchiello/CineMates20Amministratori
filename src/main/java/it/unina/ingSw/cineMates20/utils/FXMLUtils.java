package it.unina.ingSw.cineMates20.utils;

import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class FXMLUtils {

    public static Scene setRoot(String fxml) throws IOException {
        return new Scene(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        String pathFxml = Resources.get(NameResources.DIRECTORY_FXML) + "/" + fxml + ".fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(pathFxml));
        return fxmlLoader.load();
    }
}
