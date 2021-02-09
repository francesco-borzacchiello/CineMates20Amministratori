package it.unina.ingSw.cineMates20.controller;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import it.unina.ingSw.cineMates20.model.ReportHttpRequests;
import it.unina.ingSw.cineMates20.model.ReportUserDB;
import it.unina.ingSw.cineMates20.model.UserDB;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import it.unina.ingSw.cineMates20.view.GridPaneGenerator;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.util.stream.Collectors.toMap;

public class ReportUsersContainerController extends Controller{

    @FXML
    private VBox emptyDialogVBox;
    @FXML
    private Label emptyDialogLabel,
                  iconBack,
                  iconSort,
                  searchBoxLabel;
    @FXML
    private CustomTextField searchBoxCustomTextField;
    @FXML
    private ScrollPane containerScrollPane;

    private Map<UserDB, List<String>> sortedUsersMapByName,
                                      sortedUsersMapByReportsNum;

    private GridPane usersByNameGridPane,
                     usersByReportsNumGridPane;

    private boolean sortedByReportsNumber;

    @FXML
    @Override
    protected void initialize() {
        initializeIcons();
        setUpIconsListeners();

        searchBoxCustomTextField.textProperty().addListener((observable, oldValue, newValue) -> onEnter());

        List<ReportUserDB> reportedUsersDB = new ReportHttpRequests().getAllReportedUsers();
        if(reportedUsersDB.size() == 0) {
            showEmptyReports();
            return;
        }
        else emptyDialogVBox.setManaged(false);

        sortedUsersMapByReportsNum = new LinkedHashMap<>(); //Preserva ordine di inserimento, utile per future rimozioni

        UserDB actualUser;
        sortedUsersMapByName = new LinkedHashMap<>();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        String url = Resources.get(NameResources.DB_PATH) + "User/getById/{email}";

        for(ReportUserDB reportedUserDB: reportedUsersDB) {
            actualUser = restTemplate.getForObject(url, UserDB.class, reportedUserDB.getFKUtenteSegnalato());

            if(sortedUsersMapByReportsNum.get(actualUser) == null) {
                ArrayList<String> list = new ArrayList<>();
                list.add(reportedUserDB.getFKUtenteSegnalatore());
                sortedUsersMapByReportsNum.put(actualUser, list); //Inizialmente le mappe non sono ordinate
                sortedUsersMapByName.put(actualUser, list);
            }
            else {
                sortedUsersMapByReportsNum.get(actualUser).add(reportedUserDB.getFKUtenteSegnalatore());
                sortedUsersMapByName.get(actualUser).add(reportedUserDB.getFKUtenteSegnalatore());
            }
        }

        initializeMapByName();
        initializeMapByReportsNum();

        usersByReportsNumGridPane = GridPaneGenerator.generateUsersGridPane(sortedUsersMapByReportsNum);

        //Di default viene mostrato ordinamento decrescente per numero di segnalazioni
        containerScrollPane.setContent(usersByReportsNumGridPane);
        sortedByReportsNumber = true;
    }

    @FXML
    private void onEnter(){
        String query = searchBoxCustomTextField.getText().trim();

        if(query.equals("")) {
            if(sortedByReportsNumber)
                containerScrollPane.setContent(usersByReportsNumGridPane);
            else
                containerScrollPane.setContent(usersByNameGridPane);

            if(emptyDialogVBox.isVisible())
                hideEmptySearch();

            iconSort.setVisible(true);

            return;
        }

        iconSort.setVisible(false);

        Map<UserDB, List<String>> queryMap = new LinkedHashMap<>();

        /* Si sceglie di iterare su sortedMoviesMapByReportsNum tuttavia è indifferente
         * su quale delle due si itera, essendo i contenuti delle due mappe gli stessi */
        for(Map.Entry<UserDB, List<String>> entry: sortedUsersMapByReportsNum.entrySet()) {
            UserDB actualUser = entry.getKey();
            if(containsIgnoreCase(actualUser.getNome(), query)
                || containsIgnoreCase(actualUser.getCognome(), query)
                || containsIgnoreCase(actualUser.getUsername(), query))
                queryMap.put(entry.getKey(), entry.getValue());
        }

        if(queryMap.size() > 0) {
            if(emptyDialogVBox.isVisible())
                hideEmptySearch();

            containerScrollPane.setContent(GridPaneGenerator.generateUsersGridPane(queryMap));
        }
        else
            showEmptySearch();
    }

    private static boolean containsIgnoreCase(String string1, String string2) {
        final int length = string2.length();
        if (length == 0)
            return true;

        final char firstLo = Character.toLowerCase(string2.charAt(0));
        final char firstUp = Character.toUpperCase(string2.charAt(0));

        for (int i = string1.length() - length; i >= 0; i--) {
            final char ch = string1.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (string1.regionMatches(true, i, string2, 0, length))
                return true;
        }

        return false;
    }

    private void showEmptyReports() {
        searchBoxCustomTextField.setDisable(true);
        iconSort.setVisible(false);
        containerScrollPane.setVisible(false);
        containerScrollPane.setManaged(false);

        emptyDialogVBox.setVisible(true);
    }

    private void showEmptySearch() {
        emptyDialogLabel.setText("La ricerca non ha prodotto risultati.");

        iconSort.setVisible(false);
        containerScrollPane.setVisible(false);
        containerScrollPane.setManaged(false);

        emptyDialogVBox.setVisible(true);
        emptyDialogVBox.setManaged(true);
    }

    private void hideEmptySearch() {
        iconSort.setVisible(true);
        containerScrollPane.setVisible(true);
        containerScrollPane.setManaged(true);

        emptyDialogVBox.setVisible(false);
        emptyDialogVBox.setManaged(false);
    }

    private void setUpIconsListeners() {
        iconBack.setOnMouseClicked(mouseEvent -> {
            if(!searchBoxCustomTextField.getText().trim().equals("")) {
                searchBoxCustomTextField.clear();
                containerScrollPane.requestFocus(); //Rimuove focus da searchBoxCustomTextField
                onEnter(); //Resetta gridPane
            }
            else {
                try {
                    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                    new HomeController().start();
                    stage.close();
                } catch (IOException ignore) {}
            }
        });

        iconSort.setOnMouseClicked(mouseEvent -> {
            if(sortedByReportsNumber) {
                if(usersByNameGridPane == null)
                    usersByNameGridPane = GridPaneGenerator.generateUsersGridPane(sortedUsersMapByName);
                containerScrollPane.setContent(usersByNameGridPane);
                sortedByReportsNumber = false;
            }
            else {
                containerScrollPane.setContent(usersByReportsNumGridPane);
                sortedByReportsNumber = true;
            }
        });
    }

    //Metodo per ordinare gli utenti in base alle segnalazioni
    private void initializeMapByReportsNum() {
        sortedUsersMapByReportsNum = sortedUsersMapByReportsNum.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> -e.getValue().size()))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> { throw new AssertionError(); },
                        LinkedHashMap::new
                ));
    }

    //Metodo per ordinare gli utenti in base al loro nome e cognome
    private void initializeMapByName() {
        sortedUsersMapByName = sortedUsersMapByName.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<UserDB, List<String>> e1) -> e1.getKey().getNome())
                            .thenComparing((Map.Entry<UserDB, List<String>> e1) -> e1.getKey().getCognome()))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> { throw new AssertionError(); },
                        LinkedHashMap::new
                ));
    }

    private void initializeIcons() {
        Text icon = GlyphsDude.createIcon(FontAwesomeIcon.ARROW_LEFT, "4em");
        icon.setFill(Paint.valueOf("white"));
        iconBack.setGraphic(icon);
        icon = GlyphsDude.createIcon(FontAwesomeIcon.SORT, "4em");
        icon.setFill(Paint.valueOf("white"));
        iconSort.setGraphic(icon);
        icon = GlyphsDude.createIcon(FontAwesomeIcon.SEARCH, "1.5em");
        icon.setFill(Paint.valueOf("rgb(150,150,150)"));
        searchBoxLabel.setGraphic(icon);
    }
}