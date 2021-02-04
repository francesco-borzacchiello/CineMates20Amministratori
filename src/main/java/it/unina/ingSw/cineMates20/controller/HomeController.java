package it.unina.ingSw.cineMates20.controller;

import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.utils.FXMLUtils;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController extends Controller{


    @FXML
    private HBox container;

    /*@FXML
    private VBox navigationMenuVBox;
*/
    @FXML
    private HBox advHBox;

    private Stage homeStage;

    //private static boolean isInitialize = false;

    @FXML
    @Override
    protected void initialize() {
        /*if(!isInitialize)
            isInitialize = true;
        else
            return;
        */

        Node item1 = null, item2=null;
        try {
            item1 = FXMLLoader.load(App.class.getResource("FXML/navigation_menu.fxml"));;
            //navigationMenuVBox = FXMLLoader.load(App.class.getResource("FXML/navigation_menu.fxml"));
            item2 = FXMLLoader.load(App.class.getResource("FXML/login.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //advHBox.setDisable(true);

        /*AnchorPane.setLeftAnchor(item1, 0.0);
        AnchorPane.setTopAnchor(item1, 0.0);
        AnchorPane.setBottomAnchor(item1, 0.0);
        AnchorPane.setRightAnchor(item1, 1800.0);*/
        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().add(item1);
        //container.getChildren().add(item2);
        /*try {
            container.getChildren().add(FXMLUtils.loadFXML("navigation_menu"));
            //container.getChildren().add(FXMLUtils.loadFXML("prova"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void setStage(Stage stage) {
        homeStage = stage;
    }

    public void start() throws IOException {
        homeStage.setScene(FXMLUtils.setRoot(Resources.get(NameResources.HOME_LAYOUT)));
        homeStage.setMaximized(true);

        homeStage.show();

        /*homeStage.setScene(FXMLUtils.setRoot(Resources.get(NameResources.HOME_LAYOUT)));
        //homeStage.setTitle("Login - CineMates20 Pannello Amministratori");
        homeStage.show();*/
    }
}