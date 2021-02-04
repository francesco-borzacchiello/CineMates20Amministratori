package it.unina.ingSw.cineMates20.utils;

import javafx.scene.control.Alert;

public class MessageDialog {

    private MessageDialog(){}

    public static void error(String title, String message) {
        message(title, message, Alert.AlertType.ERROR);
    }

    public static void warning(String title, String message) {
        message(title, message, Alert.AlertType.WARNING);
    }

    public static void info(String title, String message) {
        message(title, message, Alert.AlertType.INFORMATION);
    }

    public static void message(String title, String message, Alert.AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
