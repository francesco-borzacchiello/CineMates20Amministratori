package it.unina.ingSw.cineMates20.controller;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unina.ingSw.cineMates20.model.LoginModel;
import it.unina.ingSw.cineMates20.utils.FXMLUtils;
import it.unina.ingSw.cineMates20.view.MessageDialog;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginController extends Controller{

    private static final Pattern
            VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE),
            VALID_PASSWORD_REGEX =
                    Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_-])(?=\\S+$).{8,}$");

    @FXML
    private Hyperlink resetPasswordHyperLink;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private Stage loginStage;

    private boolean emailValid, passwordValid;

    private final LoginModel loginModel = new LoginModel();

    public void start(Stage stage) throws IOException {
        loginStage = stage;
        loginStage.setScene(FXMLUtils.setRoot(Resources.get(NameResources.LOGIN_LAYOUT)));
        loginStage.setTitle("Login - CineMates20 Pannello Amministratori");

        File file = new File("src/main/resources/it/unina/ingSw/cineMates20/CSS/image/logo.png");
        stage.getIcons().add(new Image(file.toURI().toString()));

        loginStage.show();

        centerStage();
    }

    public void start() throws IOException {
        loginStage = new Stage();
        loginStage.setScene(FXMLUtils.setRoot(Resources.get(NameResources.LOGIN_LAYOUT)));
        loginStage.setTitle("Login - CineMates20 Pannello Amministratori");

        File file = new File("src/main/resources/it/unina/ingSw/cineMates20/CSS/image/logo.png");
        loginStage.getIcons().add(new Image(file.toURI().toString()));

        loginStage.show();

        centerStage();      //é importante che stia dopo lo show perchè usa le dimensioni dello stage
    }

    private void centerStage() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        loginStage.setX((screenBounds.getWidth() - loginStage.getWidth()) / 2);
        loginStage.setY((screenBounds.getHeight() - loginStage.getHeight()) / 2);
    }

    @FXML
    @Override
    public void initialize() {
        loginButton.setDisable(false);
        addEventListener();
    }

    private void addEventListener() {
        addEventListenerToEmailTextField();
        addEventListenerToPasswordField();
        addEventListenerToLoginButton();
        addEventListenerToChangePassword();
    }

    private void addEventListenerToLoginButton(){

        loginButton.setOnAction(event -> {
            loginStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            try {
                if(loginModel.login(emailTextField.getText(), passwordField.getText()))
                    new HomeController().start(true);
                else
                    MessageDialog.info("Credenziali errate", "Email e password non corrispondono!!");
            }catch(Exception e){
                //loginStage.close();
                MessageDialog.error("Si è verificato un errore",
                        "Si è verificato un errore, riprova tra qualche minuto a riaprire l'applicativo!!");
            }
            loginStage.close();
        });
    }

    private void addEventListenerToPasswordField() {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = VALID_PASSWORD_REGEX.matcher(passwordField.getText());
            passwordValid = matcher.find();
            showError(!passwordValid, passwordField);
            loginButton.setDisable(!(emailValid && passwordValid));
        });
    }

    private void addEventListenerToEmailTextField() {
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailTextField.getText());
            emailValid = matcher.find();
            showError(!emailValid, emailTextField);
            loginButton.setDisable(!(emailValid && passwordValid));
        });
    }

    private void addEventListenerToChangePassword() {
        resetPasswordHyperLink.setOnAction(event ->
                MessageDialog.info("Reset password", "Per il recupero della password rivolgersi ad un amministratore."));
    }

    private void showError(boolean show, TextField textField) {
        if(show)
            textField.getStyleClass().add(Resources.get(NameResources.CSS_CLASS_INPUT_ERROR));
        else
            textField.getStyleClass().removeIf(style ->
                    style.equals(Resources.get(NameResources.CSS_CLASS_INPUT_ERROR)));
    }
}