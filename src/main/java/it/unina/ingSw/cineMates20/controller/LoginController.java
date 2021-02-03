package it.unina.ingSw.cineMates20.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unina.ingSw.cineMates20.FXMLUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE),
                                 VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private Stage loginStage;

    private boolean emailValid, passwordValid;

    //private final LoginModel loginModel;

    public void start() throws IOException {
        loginStage.setScene(FXMLUtils.setRoot("login"));
        loginStage.setTitle("Login - CineMates20 Pannello Amministratori");
        loginStage.show();
    }
    
    //TODO:Creare una super classe Controller con una definizione di abstract initialize();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setDisable(false);
        addEventListenerToEmailTextField();
        addEventListenerToPasswordField();
        addEventListenerToLoginButton();

    }

    private void addEventListenerToLoginButton(){
        loginButton.setOnAction(event -> {
            loginStage  = (Stage) ((Node)event.getSource()).getScene().getWindow();
            try {
                loginStage.setScene(FXMLUtils.setRoot("hom"));
            }catch(Exception e){
                loginStage.close();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Si è verificato un errore");
                alert.setHeaderText(null);
                alert.setContentText("Si è verificato un errore, riprova tra qualche minuto a riaprire l'applicativo!!");
                alert.showAndWait();
            }
        });
    }

    private void addEventListenerToPasswordField() {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = VALID_PASSWORD_REGEX.matcher(passwordField.getText());
            passwordValid = matcher.find();
            if(!passwordValid)
                passwordField.getStyleClass().add("inputError");
            else
                passwordField.getStyleClass().removeIf(style -> style.equals("inputError"));
            loginButton.setDisable(!(emailValid && passwordValid));
        });
    }

    private void addEventListenerToEmailTextField() {
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailTextField.getText());
            emailValid = matcher.find();
            if(!emailValid)
                emailTextField.getStyleClass().add("inputError");
            else
                emailTextField.getStyleClass().removeIf(style -> style.equals("inputError"));
            loginButton.setDisable(!(emailValid && passwordValid));
        });
    }

    public void setStage(Stage stage) {
        loginStage = stage;
    }
}
