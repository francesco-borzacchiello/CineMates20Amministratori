package it.unina.ingSw.cineMates20.controller;

import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.model.LoginModel;
import it.unina.ingSw.cineMates20.model.S3Manager;
import it.unina.ingSw.cineMates20.model.UserDB;
import it.unina.ingSw.cineMates20.utils.Resources;
import it.unina.ingSw.cineMates20.view.MessageDialog;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class NavigationMenuController extends Controller {

    @FXML
    private HBox logOutHBox;

    @FXML
    private Label pendingReportsLabel,
                  managedReportsLabel,
                  informationLabel,
                  adminName,
                  adminSurname;

    @FXML
    private HBox pendingReportsHBox,
                 managedReportsHBox,
                 informationHBox;

    @FXML
    private ImageView profile_image;

    private final FileChooser fileChooser;

    private final S3Manager s3Manager;

    private HomeController homeController;

    public NavigationMenuController() {
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\")); //Verrà poi sovrascritta se necessario
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tutti i formati supportati","*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("Immagini *.jpg","*.jpg"),
                new FileChooser.ExtensionFilter("Immagini *.jpeg","*.jpeg"),
                new FileChooser.ExtensionFilter("Immagini *.png","*.png"));
        s3Manager = new S3Manager();
    }

    public void start(HomeController homeController) {
        addEventListeners();
        this.homeController = homeController;
    }

    @FXML
    @Override
    protected void initialize() {
        InputStream profileImageInputStream = s3Manager.getProfilePictureInputStream(Objects.requireNonNull(Resources.getEmail()));
        Image image;
        if (profileImageInputStream != null)
            image = new Image(profileImageInputStream, 450, 450, false, false);
        else {
            File file = new File("src/main/resources/it/unina/ingSw/cineMates20/CSS/image/profile_picture.png");
            image = new Image(file.toURI().toString());
        }

        profile_image.setImage(image);
        profile_image.setClip(new Circle(profile_image.getFitWidth() / 2, profile_image.getFitHeight() / 2, 50));

        pendingReportsLabel.getStyleClass().clear();
        pendingReportsLabel.getStyleClass().addAll("cyan_font_for_button_selected", "padding_1em");

        initializeBasicAdminInfo();
    }

    private void initializeBasicAdminInfo() {
        UserDB basicAdminInfo = new LoginModel().getAdminBasicInfo
                (BCrypt.hashpw(Objects.requireNonNull(Resources.getEmail()), BCrypt.gensalt()));

        if(basicAdminInfo != null) {
            adminName.setText(basicAdminInfo.getNome());
            adminSurname.setText(basicAdminInfo.getCognome());
        }
    }

    private void addEventListeners() {
        addEventListenerToLogoutButton();
        addEventListenerToPendingReportsButton();
        addEventListenerToManagedReportsButton();
        addEventListenerToInformationButton();
        addEventListenerToSetProfileImage();
    }

    private void addEventListenerToLogoutButton() {
        logOutHBox.setOnMouseClicked(mouseEvent -> {
            Stage actualStage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
            try {
                Resources.removeEmail();
                openLogin();
            } catch (IOException e) {
                MessageDialog.error("Si è verificato un errore",
                        "Si è verificato un errore, riprova tra qualche minuto a riaprire l'applicativo!!");
            }
            actualStage.close();
        });
    }

    private void addEventListenerToPendingReportsButton() {
        pendingReportsHBox.setOnMouseClicked(mouseEvent -> {
            setPendingReportsActive();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("FXML/home_report.fxml"));
            try {
                Node pendingReport = loader.load();
                ReportController reportController = loader.getController();
                reportController.startPendingReports(homeController);
                homeController.replaceHomeNode(pendingReport);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //HomeController.getHomeControllerInstance().startPendingReports();
        });
    }

    private void addEventListenerToManagedReportsButton() {
        managedReportsHBox.setOnMouseClicked(mouseEvent -> {
            setManagedReportsActive();

            FXMLLoader loader = new FXMLLoader(App.class.getResource("FXML/home_report.fxml"));
            try {
                Node pendingReport = loader.load();
                ReportController reportController = loader.getController();
                reportController.startManagedReports(homeController);
                homeController.replaceHomeNode(pendingReport);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void addEventListenerToInformationButton() {
        informationHBox.setOnMouseClicked(mouseEvent -> {
            setInformationActive();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("FXML/information.fxml"));
            try {
                Node information = loader.load();
                homeController.replaceHomeNode(information);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void addEventListenerToSetProfileImage() {
        profile_image.setOnMouseClicked(mouseEvent -> {
            Stage actualStage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
            File file = fileChooser.showOpenDialog(actualStage); //Blocca actualStage mentre fileChooser è aperto
            if(file != null) {
                //Se l'upload della nuova immagine è andato a buon fine, si aggiorna l'immagine del profilo
                if(s3Manager.uploadImage(Objects.requireNonNull(Resources.getEmail()), file))
                    profile_image.setImage(new Image(file.toURI().toString()));
            }
        });
    }

    private void openLogin() throws IOException {
        new LoginController().start();
    }

    public void setPendingReportsActive() {
        managedReportsLabel.getStyleClass().clear();
        managedReportsLabel.getStyleClass().addAll("white_font_for_button_enabled", "padding_1em");
        informationLabel.getStyleClass().clear();
        informationLabel.getStyleClass().addAll("white_font_for_button_enabled", "padding_1em");

        pendingReportsLabel.getStyleClass().clear();
        pendingReportsLabel.getStyleClass().addAll("cyan_font_for_button_selected", "padding_1em");
    }

    public void setManagedReportsActive() {
        pendingReportsLabel.getStyleClass().clear();
        pendingReportsLabel.getStyleClass().addAll("white_font_for_button_enabled", "padding_1em");
        informationLabel.getStyleClass().clear();
        informationLabel.getStyleClass().addAll("white_font_for_button_enabled", "padding_1em");

        managedReportsLabel.getStyleClass().clear();
        managedReportsLabel.getStyleClass().addAll("cyan_font_for_button_selected", "padding_1em");
    }

    public void setInformationActive() {
        pendingReportsLabel.getStyleClass().clear();
        pendingReportsLabel.getStyleClass().addAll("white_font_for_button_enabled", "padding_1em");
        managedReportsLabel.getStyleClass().clear();
        managedReportsLabel.getStyleClass().addAll("white_font_for_button_enabled", "padding_1em");

        informationLabel.getStyleClass().clear();
        informationLabel.getStyleClass().addAll("cyan_font_for_button_selected", "padding_1em");
    }
}
