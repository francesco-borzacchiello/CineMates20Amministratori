package it.unina.ingSw.cineMates20.controller;

import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.utils.FXMLUtils;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
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
            FXMLLoader loader = new FXMLLoader(App.class.getResource("FXML/home_report.fxml"));
            reportHome = loader.load();

            ReportController reportController = loader.getController();
            reportController.start(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().add(navigationMenu);
        container.getChildren().add(reportHome);

    }

    public void start() throws IOException {
        homeStage = new Stage();
        homeStage.setScene(FXMLUtils.setRoot(Resources.get(NameResources.HOME_LAYOUT)));
        homeStage.setMaximized(true);
        homeStage.setTitle("Home - CineMates20 Pannello Amministratori");

        File file = new File("src/main/resources/it/unina/ingSw/cineMates20/CSS/image/logo.png");
        homeStage.getIcons().add(new Image(file.toURI().toString()));

        homeStage.show();
    }

    public void replaceHomeNode(Node node) {
        container.getChildren().remove(1);
        container.getChildren().add(node);
    }
}