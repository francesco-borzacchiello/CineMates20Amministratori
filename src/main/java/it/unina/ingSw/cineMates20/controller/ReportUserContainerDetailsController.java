package it.unina.ingSw.cineMates20.controller;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.model.ReportHttpRequests;
import it.unina.ingSw.cineMates20.model.ReportUserDB;
import it.unina.ingSw.cineMates20.model.S3Manager;
import it.unina.ingSw.cineMates20.model.UserDB;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import it.unina.ingSw.cineMates20.view.GridPaneGenerator;
import it.unina.ingSw.cineMates20.view.MessageDialog;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReportUserContainerDetailsController extends Controller {

    @FXML
    private VBox reportersVBox,
                 reportDetailsVBox;

    @FXML
    private Label nameLabel,
                  usernameLabel,
                  reportsCountLabel,
                  iconBack,
                  detailsIconBack,
                  dettagliUtenteSegnalatoNomeLabel,
                  dettagliUtenteSegnalatoUsernameLabel,
                  dettagliAutoreSegnalazioneNomeLabel,
                  dettagliAutoreSegnalazioneUsernameLabel,
                  motivoSegnalazione;
    @FXML
    private ImageView userImageView,
                      dettagliUtenteSegnalatoImageView,
                      dettagliAutoreSegnalazioneImageView;

    @FXML
    private ScrollPane containerScrollPane;

    private static UserDB reportedUser;
    private static List<UserDB> reporters;

    private static List<ReportUserDB> userReportedUsers; //Segnalazioni di questo utente dello stesso utente

    private static ReportUsersContainerController reportUsersContainerController;
    private static ManagedReportUsersContainerController managedReportUsersContainerController;
    private static ReportHttpRequests reportHttpRequests;

    @FXML
    private Button approvaButton,
                   rigettaButton;

    private static boolean actualReportTypeIsPending;

    @FXML
    @Override
    protected void initialize() {
        initializeIcons();
        setUpIconListeners();

        initializeReportedUser();

        //Raggruppamento segnalazioni per utenti
        reporters.sort(Comparator.comparing(UserDB::getNome)
                .thenComparing(UserDB::getCognome)
                .thenComparing(UserDB::getUsername)
                .thenComparing(UserDB::getEmail));

        initializeReportersGridPane();
        initializeVBoxes();

        reportHttpRequests = new ReportHttpRequests();
    }

    private void initializeReportersGridPane() {
        ArrayList<Runnable> listeners = new ArrayList<>();
        UserDB actualReporter = reporters.get(0);
        initializeReportReasons(actualReporter); //Inizializza la lista di segnalazioni di questo utente per questo utente

        for(UserDB reporter: reporters) {
            //Se l'utente è cambiato...
            if(!actualReporter.equals(reporter)) {
                //Si aggiorna la lista di segnalazioni di questo utente per questo utente
                initializeReportReasons(reporter);
                //L'utente attuale si aggiorna
                actualReporter = reporter;
            }
            //Nota: userReportedUsers.size() corrisponde al numero di segnalazioni di questo utente per questo utente (segnalazioni duplicate)
            listeners.add(getEventListenerForReporterUser(reporter, userReportedUsers.remove(0)));
        }

        containerScrollPane.setContent(GridPaneGenerator.generateReportersUsersGridPane(reporters, listeners));
    }

    private void initializeVBoxes() {
        reportDetailsVBox.setVisible(false);
        reportDetailsVBox.managedProperty().bind(reportDetailsVBox.visibleProperty());

        reportersVBox.managedProperty().bind(reportersVBox.visibleProperty());
    }

    private Runnable getEventListenerForReporterUser(UserDB reporter, ReportUserDB report) {
        return () ->
                Platform.runLater(() -> {
                    if(actualReportTypeIsPending) {
                        approvaButton.setOnMouseClicked(getEventListenerForReportButton(reporter, report, "Approvata"));
                        rigettaButton.setOnMouseClicked(getEventListenerForReportButton(reporter, report, "Rigettata"));
                    }
                    else {
                        disableAllReportButtons(true);

                        switch(report.getEsitoSegnalazione()) {
                            case "Rigettata": {
                                rigettaButton.setDisable(false);
                                rigettaButton.setMouseTransparent(true);
                                break;
                            }
                            case "Approvata": {
                                approvaButton.setDisable(false);
                                approvaButton.setMouseTransparent(true);
                                break;
                            }
                        }
                    }

                    initializeReportUsers(reporter);

                    reportDetailsVBox.setVisible(true);
                    reportersVBox.setVisible(false);

                    motivoSegnalazione.setText(report.getMessaggioSegnalazione());
                });
    }

    private void initializeReportUsers(UserDB reporter) {
        dettagliUtenteSegnalatoNomeLabel.setText(reportedUser.getNome());
        dettagliUtenteSegnalatoUsernameLabel.setText("@" + reportedUser.getUsername());

        S3Manager s3Manager = new S3Manager();
        Image image;

        InputStream profileImageInputStream = s3Manager.getProfilePictureInputStream(reportedUser.getEmail());
        if (profileImageInputStream != null)
            image = new Image(profileImageInputStream, 256, 256, false, false);
        else {
            File file = new File("src/main/resources/it/unina/ingSw/cineMates20/CSS/image/profile_picture.png");
            image = new Image(file.toURI().toString(), 256, 256, false, false);
        }
        dettagliUtenteSegnalatoImageView.setImage(image);
        dettagliUtenteSegnalatoImageView.setClip(new Circle(userImageView.getFitWidth() / 2, userImageView.getFitHeight() / 2, 50));

        dettagliAutoreSegnalazioneNomeLabel.setText(reporter.getNome());
        dettagliAutoreSegnalazioneUsernameLabel.setText("@" + reporter.getUsername());

        profileImageInputStream = s3Manager.getProfilePictureInputStream(reporter.getEmail());
        if (profileImageInputStream != null)
            image = new Image(profileImageInputStream, 256, 256, false, false);
        else {
            File file = new File("src/main/resources/it/unina/ingSw/cineMates20/CSS/image/profile_picture.png");
            image = new Image(file.toURI().toString(), 256, 256, false, false);
        }

        dettagliAutoreSegnalazioneImageView.setImage(image);
        dettagliAutoreSegnalazioneImageView.setClip(new Circle(userImageView.getFitWidth() / 2, userImageView.getFitHeight() / 2, 50));
    }

    private EventHandler<? super MouseEvent> getEventListenerForReportButton(UserDB reporter, ReportUserDB report, String reportOutcome) {
        return (mouseEvent -> {
            disableAllReportButtons(true);
            report.setEsitoSegnalazione(reportOutcome);
            if(!reportHttpRequests.adminUpdateUserReport(report, Resources.getEmailHash()))
                MessageDialog.error("Si è verificato un errore", "Si è verificato un errore, riprova più tardi.");

            if(reportOutcome.equals("Oscurata"))
                detailsIconBack.setOnMouseClicked((mouseEvent2 -> {
                    if (actualReportTypeIsPending)
                        reportUsersContainerController.showAgain();
                    else
                        managedReportUsersContainerController.showAgain();
                }));

            deleteSelectedUserFromReportersGridPane(reporter);

            switch(report.getEsitoSegnalazione()) {
                case "Rigettata": {
                    rigettaButton.setDisable(false);
                    rigettaButton.setMouseTransparent(true);
                    rigettaButton.setOnMouseClicked((mouseEvent2)->{});
                    break;
                }
                case "Approvata": {
                    approvaButton.setDisable(false);
                    approvaButton.setMouseTransparent(true);
                    approvaButton.setOnMouseClicked((mouseEvent2)->{});
                    break;
                }
            }
        });
    }

    private void deleteSelectedUserFromReportersGridPane(UserDB reporter) {
        if(actualReportTypeIsPending && reportUsersContainerController != null)
            reportUsersContainerController.updateReportsLayout();


        reporters.removeAll(Collections.singleton(reporter));
        reportsCountLabel.setText("Numero segnalazioni: " + reporters.size());

        if(reporters.size() > 0)
            initializeReportersGridPane();
        else
            detailsIconBack.setOnMouseClicked((mouseEvent -> {
                if(actualReportTypeIsPending)
                    reportUsersContainerController.showAgain();
                else
                    managedReportUsersContainerController.showAgain();
            }));
    }

    private void disableAllReportButtons(boolean disable) {
        approvaButton.setDisable(disable);
        rigettaButton.setDisable(disable);
    }

    private void resetAllReportButtons() {
        disableAllReportButtons(false);
        approvaButton.setMouseTransparent(false);
        rigettaButton.setMouseTransparent(false);
    }

    //Restituisce le segnalazioni di questo utente (reporter) per il film attualmente selezionato (reportedMovie)
    private void initializeReportReasons(UserDB reporter) {
        if(userReportedUsers != null)
            userReportedUsers.clear();

        if(actualReportTypeIsPending && reportUsersContainerController != null)
            userReportedUsers = reportUsersContainerController.getUserReportedUsers(reportedUser, reporter.getEmail());

        else if(!actualReportTypeIsPending && managedReportUsersContainerController != null)
            userReportedUsers = managedReportUsersContainerController.getUserReportedUsers(reportedUser, reporter.getEmail());
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
        Platform.runLater(() -> {
            nameLabel.setText(reportedUser.getNome() + " " + reportedUser.getCognome());
            reportsCountLabel.setText(reportsCountLabel.getText() + " " + reporters.size());
            usernameLabel.setText("@" + reportedUser.getUsername());

            new Thread(() -> {
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

    private void setUpIconListeners() {
        iconBack.setOnMouseClicked(mouseEvent -> {
            if (actualReportTypeIsPending)
                reportUsersContainerController.showAgain();
            else
                managedReportUsersContainerController.showAgain();
        });

        detailsIconBack.setOnMouseClicked(mouseEvent -> {
            reportDetailsVBox.setVisible(false);
            reportersVBox.setVisible(true);

            resetAllReportButtons();
        });
    }

    private void initializeIcons() {
        Text icon = GlyphsDude.createIcon(FontAwesomeIcon.ARROW_LEFT, "4em");
        icon.setFill(Paint.valueOf("white"));
        iconBack.setGraphic(icon);

        Text icon2 = GlyphsDude.createIcon(FontAwesomeIcon.ARROW_LEFT, "4em");
        icon2.setFill(Paint.valueOf("white"));
        detailsIconBack.setGraphic(icon2);
    }
}