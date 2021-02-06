package it.unina.ingSw.cineMates20.controller;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import it.unina.ingSw.cineMates20.model.S3Manager;
import it.unina.ingSw.cineMates20.utils.MessageDialog;
import it.unina.ingSw.cineMates20.utils.NameResources;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import it.unina.ingSw.cineMates20.utils.Resources;

public class NavigationMenuController extends Controller {

    @FXML
    private HBox logOutHBox;

    @FXML
    private ImageView profile_image;

    private final FileChooser fileChooser;

    private final S3Manager s3Manager;

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

    @FXML
    @Override
    protected void initialize() {
        //TODO:settare i restanti listener dei tasti attivi
        addEventListener();

        /*TmdbMovies tmdbMovies = new TmdbMovies(new TmdbApi(Resources.get(NameResources.TMDB_API_KEY)));
        MovieDb movie = tmdbMovies.getMovie(293660, "it");
        profile_image.setImage(new Image(Resources.get(NameResources.FIRST_PATH_IMAGE) + movie.getPosterPath()));*/

        InputStream profileImageInputStream = s3Manager.getProfilePictureInputStream("fran.borzacchiello@studenti.unina.it");
        if(profileImageInputStream != null)
            profile_image.setImage(new Image(profileImageInputStream));
        else {
            File file = new File("src/main/resources/it/unina/ingSw/cineMates20/CSS/image/profile_picture.png");
            profile_image.setImage(new Image(file.toURI().toString()));
        }
        profile_image.setClip(new Circle(profile_image.getFitWidth() / 2, profile_image.getFitHeight() / 2,50));
    }

    private void addEventListener() {
        addEventListenerToLogoutButton();
        addEventListenerForSetProfileImage();
    }

    private void addEventListenerToLogoutButton() {
        logOutHBox.setOnMouseClicked(mouseEvent -> {
            Stage actualStage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
            actualStage.close();
            try {
                //openLogin(actualStage);
                openLogin();
            } catch (IOException e) {
                //actualStage.close();
                MessageDialog.error("Si è verificato un errore",
                        "Si è verificato un errore, riprova tra qualche minuto a riaprire l'applicativo!!");
            }
        });
    }

    private void addEventListenerForSetProfileImage() {
        profile_image.setOnMouseClicked(mouseEvent -> {
            Stage actualStage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
            File file = fileChooser.showOpenDialog(actualStage); //Blocca actualStage mentre fileChooser è aperto
            if(file != null) {
                //Se l'upload della nuova immagine è andato a buon fine, si aggiorna l'immagine del profilo
                if(s3Manager.uploadImage("fran.borzacchiello@studenti.unina.it", file))
                    profile_image.setImage(new Image(file.toURI().toString()));
            }
        });
    }

    /*private void openLogin(Stage actualStage) throws IOException {
        LoginController loginController = new LoginController();
        loginController.setStage(actualStage);
        loginController.start();
    }*/

    private void openLogin() throws IOException {
        new LoginController().start();
    }
}
