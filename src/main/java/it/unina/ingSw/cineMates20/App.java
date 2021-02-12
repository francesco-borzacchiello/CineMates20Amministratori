package it.unina.ingSw.cineMates20;

import it.unina.ingSw.cineMates20.controller.HomeController;
import it.unina.ingSw.cineMates20.controller.LoginController;
import it.unina.ingSw.cineMates20.model.LoginModel;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(@NotNull Stage stage) throws IOException {
        if(alreadyLoggedIn())
            new HomeController().start(true);
        else
            new LoginController().start(stage);
    }

    private boolean alreadyLoggedIn() {
        String hashEmail = Resources.getEmailHash();
        LoginModel loginModel = new LoginModel();
        if(hashEmail != null && !loginModel.emailAlreadyExists(hashEmail)) {
            Resources.removeHashEmail();
            return false;
        }
        else return loginModel.emailAlreadyExists(hashEmail);
    }

    public static void main(String[] args) {
        launch();
    }
}