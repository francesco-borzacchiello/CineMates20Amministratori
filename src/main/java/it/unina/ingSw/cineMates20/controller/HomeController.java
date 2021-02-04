package it.unina.ingSw.cineMates20.controller;

import it.unina.ingSw.cineMates20.utils.FXMLUtils;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController extends Controller{

    @FXML
    private HBox container;

    private Stage homeStage;

    @Override
    protected void initialize() {
        try {
            container.getChildren().add(FXMLUtils.loadFXML("navigation_menu"));
            container.getChildren().add(FXMLUtils.loadFXML("prova"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        homeStage = stage;
    }

    public void start() {
        try {
            homeStage.setScene(FXMLUtils.setRoot(Resources.get(NameResources.HOME_LAYOUT)));
            homeStage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //homeStage.setTitle("Login - CineMates20 Pannello Amministratori");
        homeStage.show();
    }
}
