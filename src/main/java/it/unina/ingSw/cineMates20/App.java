package it.unina.ingSw.cineMates20;

import it.unina.ingSw.cineMates20.controller.LoginController;
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
        /*LoginController loginController = new LoginController();
        loginController.setStage(stage);
        loginController.start();*/
        new LoginController().start(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}