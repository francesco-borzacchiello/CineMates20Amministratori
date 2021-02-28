package it.unina.ingSw.cineMates20.controller;

import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ReportController extends Controller {

    @FXML
    private Label titleLabel;
    @FXML
    private HBox film_HBox;
    @FXML
    private HBox utenti_HBox;

    @FXML
    @Override
    protected void initialize() {}

    public void startPendingReports(HomeController homeController) {
        film_HBox.setOnMouseClicked(mouseEvent -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Resources.get(NameResources.DIRECTORY_FXML) + "/" +
                        "report_movies_container" + ".fxml"));
                Node reportMoviesContainer = loader.load();
                ReportMoviesContainerController reportMoviesContainerController = loader.getController();
                reportMoviesContainerController.setHomeController(homeController);
                reportMoviesContainerController.setReportMoviesContainerNode(reportMoviesContainer);

                homeController.replaceHomeNode(reportMoviesContainer);
            } catch (IOException ignore) {}
        });
        utenti_HBox.setOnMouseClicked(mouseEvent -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Resources.get(NameResources.DIRECTORY_FXML) + "/" +
                        "report_users_container" + ".fxml"));
                Node reportUsersContainer = loader.load();
                ReportUsersContainerController reportUsersContainerController = loader.getController();
                reportUsersContainerController.setHomeController(homeController);
                reportUsersContainerController.setReportUsersContainerNode(reportUsersContainer);

                homeController.replaceHomeNode(reportUsersContainer);
            } catch (IOException ignore) {}
        });
    }

    public void startManagedReports(HomeController homeController) {
        titleLabel.setText("Scegliere il tipo di segnalazioni che sono state gestite:    ");
        film_HBox.setOnMouseClicked(mouseEvent -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Resources.get(NameResources.DIRECTORY_FXML) + "/" +
                        "managed_report_movies_container" + ".fxml"));
                Node managedReportsMoviesContainer = loader.load();
                ManagedReportsMoviesContainerController managedReportsMoviesContainerController = loader.getController();
                managedReportsMoviesContainerController.setHomeController(homeController);
                managedReportsMoviesContainerController.setManagedReportsMoviesContainerNode(managedReportsMoviesContainer);

                homeController.replaceHomeNode(managedReportsMoviesContainer);
            } catch (IOException ignore) {}
        });
        utenti_HBox.setOnMouseClicked(mouseEvent -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Resources.get(NameResources.DIRECTORY_FXML) + "/" +
                        "managed_report_users_container" + ".fxml"));
                Node managedReportUsersContainer = loader.load();
                ManagedReportUsersContainerController managedReportUsersContainerController = loader.getController();
                managedReportUsersContainerController.setHomeController(homeController);
                managedReportUsersContainerController.setManagedReportUsersContainerNode(managedReportUsersContainer);

                homeController.replaceHomeNode(managedReportUsersContainer);
            } catch (IOException ignore) {}
        });
    }
}
