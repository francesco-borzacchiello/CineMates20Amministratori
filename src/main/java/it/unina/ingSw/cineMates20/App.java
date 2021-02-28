package it.unina.ingSw.cineMates20;

import it.unina.ingSw.cineMates20.controller.HomeController;
import it.unina.ingSw.cineMates20.controller.LoginController;
import it.unina.ingSw.cineMates20.model.LoginModel;
import it.unina.ingSw.cineMates20.utils.Resources;

import javafx.application.Application;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCrypt;

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
        String email = Resources.getEmail();
        if(email == null) return false;

        String emailHash = BCrypt.hashpw(email, BCrypt.gensalt());
        LoginModel loginModel = new LoginModel();

        if(!loginModel.emailAlreadyExists(emailHash)) {
            Resources.removeEmail();
            return false;
        }
        else
            return true;
    }

    public static void main(String[] args) {
        launch();
    }
}