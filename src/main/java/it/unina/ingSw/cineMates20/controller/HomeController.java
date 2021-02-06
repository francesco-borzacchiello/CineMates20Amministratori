package it.unina.ingSw.cineMates20.controller;

import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.utils.FXMLUtils;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController extends Controller{

    @FXML
    private HBox container;

    private Stage homeStage;

    @FXML
    @Override
    protected void initialize() {
        Node navigationMenu = null, reportHome = null;
        try {
            navigationMenu = FXMLUtils.loadFXML(Resources.get(NameResources.NAVIGATION_MENU_LAYOUT));
            reportHome = FXMLLoader.load(App.class.getResource("FXML/home_report.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*AnchorPane.setLeftAnchor(item1, 0.0);
        AnchorPane.setTopAnchor(item1, 0.0);
        AnchorPane.setBottomAnchor(item1, 0.0);
        AnchorPane.setRightAnchor(item1, 1800.0);*/
        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().add(navigationMenu);
        container.getChildren().add(reportHome);
        /*try {
            container.getChildren().add(FXMLUtils.loadFXML("navigation_menu"));
            //container.getChildren().add(FXMLUtils.loadFXML("prova"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /*public void setStage(Stage stage) {
        homeStage = stage;
    }*/

    public void start() throws IOException {
        //Stage homeStage = new Stage();
        //this.homeStage = homeStage;
        homeStage = new Stage();
        homeStage.setScene(FXMLUtils.setRoot(Resources.get(NameResources.HOME_LAYOUT)));
        homeStage.setMaximized(true);
        homeStage.setTitle("Home - CineMates20 Pannello Amministratori");
        homeStage.show();

        /*homeStage.setScene(FXMLUtils.setRoot(Resources.get(NameResources.HOME_LAYOUT)));
        //homeStage.setTitle("Login - CineMates20 Pannello Amministratori");
        homeStage.show();*/
    }
}