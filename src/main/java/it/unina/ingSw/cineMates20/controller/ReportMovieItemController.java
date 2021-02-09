package it.unina.ingSw.cineMates20.controller;

import info.movito.themoviedbapi.model.MovieDb;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;


public class ReportMovieItemController extends Controller {

    @FXML
    private HBox movieHBox;
    @FXML
    private ImageView movieImageView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label reportsCountLabel;

    private String IMAGE_FIRST_PATH;

    @FXML
    @Override
    protected void initialize() {}

    public void start(MovieDb movie, int numSegnalazioni) {
        IMAGE_FIRST_PATH = Resources.get(NameResources.FIRST_PATH_IMAGE);

        Platform.runLater(()-> {
            if (movie.getTitle() != null)
                titleLabel.setText(movie.getTitle());
            else
                titleLabel.setText(movie.getOriginalTitle());

            if(movie.getPosterPath() != null)
                new Thread(()-> {
                    Image image = new Image(IMAGE_FIRST_PATH + movie.getPosterPath(), 185, 278, false, false);
                    Platform.runLater(()-> movieImageView.setImage(image));
                }).start();

            Platform.runLater(() -> {
                Rectangle rectangle = new Rectangle(movieImageView.getFitWidth(), movieImageView.getFitHeight());
                rectangle.setArcHeight(20.0);
                rectangle.setArcWidth(20.0);
                movieImageView.setClip(rectangle);
            });

            reportsCountLabel.setText(reportsCountLabel.getText() + " " + numSegnalazioni);

            //TODO: listener movieHBox
        });
    }
}
