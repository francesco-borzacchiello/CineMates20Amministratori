package it.unina.ingSw.cineMates20.view;

import info.movito.themoviedbapi.model.MovieDb;
import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.controller.ReportMovieItemController;
import it.unina.ingSw.cineMates20.controller.ReportUserItemController;
import it.unina.ingSw.cineMates20.model.UserDB;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Map;

public class GridPaneGenerator {

    private static final int COLUMN_NUM = 3;

    public static GridPane generateMoviesGridPane(Map<MovieDb, List<String>> reportedMoviesMap) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        int index = 0;
        for(MovieDb movie: reportedMoviesMap.keySet()) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource
                        (Resources.get(NameResources.DIRECTORY_FXML) + "/" + Resources.get(NameResources.REPORT_MOVIE_ITEM_LAYOUT) + ".fxml"));
                Node item = loader.load();
                ReportMovieItemController reportMovieItemController = loader.getController();

                reportMovieItemController.start(movie, reportedMoviesMap.get(movie).size());

                gridPane.add(item, index % COLUMN_NUM, index / COLUMN_NUM);
                index++;

                //TODO: for each su movies per il set dei listener
            }catch(Exception ignore){}
        }

        return gridPane;
    }

    public static GridPane generateUsersGridPane(Map<UserDB, List<String>> reportedUsersMap) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        int index = 0;
        for(UserDB user: reportedUsersMap.keySet()) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource
                        (Resources.get(NameResources.DIRECTORY_FXML) + "/" + Resources.get(NameResources.REPORT_USER_ITEM_LAYOUT) + ".fxml"));
                Node item = loader.load();
                ReportUserItemController reportUserItemController = loader.getController();

                reportUserItemController.start(user, reportedUsersMap.get(user).size());

                gridPane.add(item, index % COLUMN_NUM, index / COLUMN_NUM);
                index++;

                //TODO: for each sugli utenti per il set dei listener
            }catch(Exception ignore){}
        }

        return gridPane;
    }
}