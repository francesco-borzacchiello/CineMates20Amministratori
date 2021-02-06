package it.unina.ingSw.cineMates20.controller;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import it.unina.ingSw.cineMates20.utils.FXMLUtils;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import it.unina.ingSw.cineMates20.view.GridPaneGenerator;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ReportController extends Controller {

    @FXML
    private HBox film_HBox;
    @FXML
    private HBox utenti_HBox;

    @FXML
    @Override
    protected void initialize() {}

    public void start(HomeController homeController) {
        film_HBox.setOnMouseClicked(mouseEvent -> {
            try {
                homeController.setHome(FXMLUtils.loadFXML("report_movies_container"));
            } catch (IOException ignore) {}

        });
    }
}
