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

    private ReportController reportController;

    private NavigationMenuController navigationMenuController;
    private static boolean startPendingReports;

    public HomeController () {}

    @FXML
    @Override
    protected void initialize() {
        Node navigationMenu, reportHome;
        try {
            //navigationMenu = FXMLUtils.loadFXML(Resources.get(NameResources.NAVIGATION_MENU_LAYOUT));

            FXMLLoader loader = new FXMLLoader(App.class.getResource(Resources.get(NameResources.DIRECTORY_FXML) + "/" + Resources.get(NameResources.NAVIGATION_MENU_LAYOUT) + ".fxml"));
            navigationMenu = loader.load();
            navigationMenuController = loader.getController();
            navigationMenuController.start(this);

            loader = new FXMLLoader(App.class.getResource("FXML/home_report.fxml"));
            reportHome = loader.load();
            reportController = loader.getController();
            if(startPendingReports) {
                reportController.startPendingReports(this);
                navigationMenuController.setPendingReportsActive();
            } else {
                reportController.startManagedReports(this);
                navigationMenuController.setManagedReportsActive();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        container.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().add(navigationMenu);
        container.getChildren().add(reportHome);
    }

    public void start(boolean startPendingReports) throws IOException {
        this.startPendingReports = startPendingReports;
        homeStage = new Stage();
        /*if(homeStage.getScene() != null)
            homeStage.getScene().setRoot(FXMLUtils.loadFXML(Resources.get(NameResources.HOME_LAYOUT)));
        else
            homeStage.setScene(FXMLUtils.setRoot(Resources.get(NameResources.HOME_LAYOUT)));*/

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