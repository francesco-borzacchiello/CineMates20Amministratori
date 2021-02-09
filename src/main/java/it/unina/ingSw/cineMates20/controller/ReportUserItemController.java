package it.unina.ingSw.cineMates20.controller;

import it.unina.ingSw.cineMates20.model.S3Manager;
import it.unina.ingSw.cineMates20.model.UserDB;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import java.io.File;
import java.io.InputStream;


public class ReportUserItemController extends Controller {

    @FXML
    private HBox userHBox;
    @FXML
    private ImageView userImageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label reportsCountLabel;

    @FXML
    @Override
    protected void initialize() {}

    public void start(UserDB user, int numSegnalazioni) {
        Platform.runLater(()-> {
            nameLabel.setText(user.getNome() + " " + user.getCognome());
            reportsCountLabel.setText(reportsCountLabel.getText() + " " + numSegnalazioni);
            usernameLabel.setText("@" + user.getUsername());

            new Thread(()-> {
                InputStream profileImageInputStream = new S3Manager().getProfilePictureInputStream(user.getEmail());
                Image image;
                if (profileImageInputStream != null)
                    image = new Image(profileImageInputStream, 256, 256, false, false);
                else {
                    File file = new File("src/main/resources/it/unina/ingSw/cineMates20/CSS/image/profile_picture.png");
                    image = new Image(file.toURI().toString(), 256, 256, false, false);
                }

                Platform.runLater(()-> {
                    userImageView.setImage(image);
                    userImageView.setClip(new Circle(userImageView.getFitWidth() / 2, userImageView.getFitHeight() / 2, 50));
                });
            }).start();

            //TODO: listener userHBox
        });
    }
}
