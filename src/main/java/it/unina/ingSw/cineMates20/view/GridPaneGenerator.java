package it.unina.ingSw.cineMates20.view;

import info.movito.themoviedbapi.model.MovieDb;
import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.controller.ReportMovieItemController;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.List;

public class GridPaneGenerator {

    private static final int COLUMN_NUM = 3;

    public static GridPane generateMovieGridPane(List<MovieDb> movies) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        int index = 0;
        for(MovieDb movie: movies) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(Resources.get(NameResources.DIRECTORY_FXML) + "/" + Resources.get(NameResources.REPORT_ITEM_LAYOUT) + ".fxml"));
                Node item = loader.load();
                ReportMovieItemController reportMovieItemController = loader.getController();
                reportMovieItemController.start(movie);

                gridPane.add(item, index % COLUMN_NUM, index / COLUMN_NUM);
                index++;
                //TODO: for each su movies per il set dei listener
            }catch(Exception ignore){}
        }

        return gridPane;
    }
}