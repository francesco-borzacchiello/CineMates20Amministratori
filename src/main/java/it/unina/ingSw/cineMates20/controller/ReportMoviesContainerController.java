package it.unina.ingSw.cineMates20.controller;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import it.unina.ingSw.cineMates20.view.GridPaneGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

import java.util.LinkedList;
import java.util.List;

public class ReportMoviesContainerController extends Controller{

    @FXML
    private ScrollPane containerScrollPane;

    @FXML
    @Override
    protected void initialize() {
        TmdbMovies tmdbMovies = new TmdbApi(Resources.get(NameResources.TMDB_API_KEY)).getMovies();
        MovieResultsPage topRated = tmdbMovies.getTopRatedMovies("it",1);
        List<MovieDb> movies = new LinkedList<>(topRated.getResults());

        containerScrollPane.setContent(GridPaneGenerator.generateMovieGridPane(movies));
    }

    public void start(HomeController homeController) {

    }
}
