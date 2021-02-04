package it.unina.ingSw.cineMates20.controller;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unina.ingSw.cineMates20.model.LoginModel;
import it.unina.ingSw.cineMates20.utils.FXMLUtils;
import it.unina.ingSw.cineMates20.utils.MessageDialog;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController extends Controller{

    private static final Pattern
            VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE),
            VALID_PASSWORD_REGEX =
                    Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_-])(?=\\S+$).{8,}$");

    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private Stage loginStage;

    private boolean emailValid, passwordValid;

    private final LoginModel loginModel = new LoginModel();

    public void setStage(Stage stage) {
        loginStage = stage;
    }

    public void start() throws IOException {
        loginStage.setScene(FXMLUtils.setRoot(Resources.get(NameResources.LOGIN_LAYOUT)));
        loginStage.setTitle("Login - CineMates20 Pannello Amministratori");
        loginStage.show();
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
    }

    private void addEventListenerToLoginButton(){
        loginButton.setOnAction(event -> {
            loginStage  = (Stage) ((Node)event.getSource()).getScene().getWindow();
            try {
                if(loginModel.login(emailTextField.getText(), passwordField.getText())) {
                    HomeController homeController = new HomeController();
                    homeController.setStage(loginStage);
                    homeController.start();
                }else
                    MessageDialog.info("Credenziali errate", "Email e password non corrispondono!!");
            }catch(Exception e){
                loginStage.close();
                MessageDialog.error("Si è verificato un errore",
                        "Si è verificato un errore, riprova tra qualche minuto a riaprire l'applicativo!!");
            }
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

    private void showError(boolean show, TextField textField) {
        if(show)
            textField.getStyleClass().add(Resources.get(NameResources.CSS_CLASS_INPUT_ERROR));
        else
            textField.getStyleClass().removeIf(style ->
                    style.equals(Resources.get(NameResources.CSS_CLASS_INPUT_ERROR)));
    }
}