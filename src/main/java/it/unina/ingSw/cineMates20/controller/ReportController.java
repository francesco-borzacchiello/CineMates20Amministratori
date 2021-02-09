package it.unina.ingSw.cineMates20.controller;

import it.unina.ingSw.cineMates20.utils.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.io.IOException;

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
                homeController.replaceHomeNode(FXMLUtils.loadFXML("report_movies_container"));
            } catch (IOException ignore) {}
        });
        utenti_HBox.setOnMouseClicked(mouseEvent -> {
            try {
                homeController.replaceHomeNode(FXMLUtils.loadFXML("report_users_container"));
            } catch (IOException ignore) {}
        });
    }
}
