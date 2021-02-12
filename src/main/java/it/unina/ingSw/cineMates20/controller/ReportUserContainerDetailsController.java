package it.unina.ingSw.cineMates20.controller;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.model.S3Manager;
import it.unina.ingSw.cineMates20.model.UserDB;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import it.unina.ingSw.cineMates20.view.GridPaneGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReportUserContainerDetailsController extends Controller{

    @FXML
    private Label nameLabel,
                  usernameLabel,
                  reportsCountLabel,
                  iconBack;
    @FXML
    private ImageView userImageView;

    @FXML
    private ScrollPane containerScrollPane;

    private static GridPane reportersGridPane; //Necessario per comunicare future rimozioni dalla lista di segnalazioni, se actualReportTypeIsPending = true

    private static UserDB reportedUser;
    private static List<UserDB> reporters;

    private static ReportUsersContainerController reportUsersContainerController;
    private static ManagedReportUsersContainerController managedReportUsersContainerController;

    private static boolean actualReportTypeIsPending;

    @FXML
    @Override
    protected void initialize() {
        initializeBackIcon();
        setUpBackIconListener();

        initializeReportedUser();

        ArrayList<Runnable> listeners = new ArrayList<>();

        for(UserDB reporter: reporters)
            listeners.add(getEventListenerForReporterUser(reporter));

        reportersGridPane = GridPaneGenerator.generateReportersUsersGridPane(reporters, listeners);

        containerScrollPane.setContent(reportersGridPane);
    }

    private Runnable getEventListenerForReporterUser(UserDB reporter) {
        return () -> {
            //TODO: settare listener per l'apertura del controller associato alla prossima schermata
        };
    }

    public void startPendingReports(UserDB actualUser, List<UserDB> reporters, HomeController homeController,
                                    ReportUsersContainerController reportUsersContainerController) throws IOException {
        ReportUserContainerDetailsController.reportedUser = actualUser;
        ReportUserContainerDetailsController.reporters = reporters;
        ReportUserContainerDetailsController.reportUsersContainerController = reportUsersContainerController; //Necessario per riferire eliminazione segnalazione
        actualReportTypeIsPending = true;

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Resources.get(NameResources.DIRECTORY_FXML) + "/" +
                Resources.get(NameResources.REPORT_USERS_CONTAINER_DETAILS_LAYOUT) + ".fxml"));

        homeController.replaceHomeNode(fxmlLoader.load());
    }

    public void startManagedReports(UserDB reportedUser, List<UserDB> reporters, HomeController homeController,
                                    ManagedReportUsersContainerController managedReportUsersContainerController) throws IOException {
        ReportUserContainerDetailsController.reportedUser = reportedUser;
        ReportUserContainerDetailsController.reporters = reporters;
        ReportUserContainerDetailsController.managedReportUsersContainerController = managedReportUsersContainerController; //Necessario per riferire eliminazione segnalazione
        actualReportTypeIsPending = false;

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Resources.get(NameResources.DIRECTORY_FXML) + "/" +
                Resources.get(NameResources.REPORT_USERS_CONTAINER_DETAILS_LAYOUT) + ".fxml"));

        homeController.replaceHomeNode(fxmlLoader.load());
    }

    private void initializeReportedUser() {
        Platform.runLater(()-> {
            nameLabel.setText(reportedUser.getNome() + " " + reportedUser.getCognome());
            reportsCountLabel.setText(reportsCountLabel.getText() + " " + reporters.size());
            usernameLabel.setText("@" + reportedUser.getUsername());

            new Thread(()-> {
                InputStream profilePictureInputStream = new S3Manager().getProfilePictureInputStream(reportedUser.getEmail());
                Image image;
                if (profilePictureInputStream != null) {
                    image = new Image(profilePictureInputStream, 256, 256, false, false);

                    Platform.runLater(() -> {
                        userImageView.setImage(image);
                        userImageView.setClip(new Circle(userImageView.getFitWidth() / 2, userImageView.getFitHeight() / 2, 50));
                    });
                }
            }).start();
        });
    }

    private void setUpBackIconListener() {
        iconBack.setOnMouseClicked(mouseEvent -> {
            if(actualReportTypeIsPending)
                reportUsersContainerController.showAgain();
            else
                managedReportUsersContainerController.showAgain();
        });
    }

    private void initializeBackIcon() {
        Text icon = GlyphsDude.createIcon(FontAwesomeIcon.ARROW_LEFT, "4em");
        icon.setFill(Paint.valueOf("white"));
        iconBack.setGraphic(icon);
    }
}