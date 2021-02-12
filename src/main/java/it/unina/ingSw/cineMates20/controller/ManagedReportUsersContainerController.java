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

import static java.util.stream.Collectors.toMap;

public class ManagedReportUsersContainerController extends Controller{

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

    private Map<UserDB, List<ReportUserDB>> sortedUsersMapByName,
                                            sortedUsersMapByReportsNum;

    private GridPane usersByNameGridPane,
                     usersByReportsNumGridPane;

    private ArrayList<Runnable> usersByNameRunnables;

    private boolean sortedByReportsNumber;

    private HomeController homeController;

    private final RestTemplate restTemplate;
    private final String getUserUrl;

    private Node managedReportUserContainerNode;

    public ManagedReportUsersContainerController() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        getUserUrl = Resources.getDbPath() + "User/getById/{email}";
    }

    @FXML
    @Override
    protected void initialize() {
        initializeIcons();
        setUpIconsListeners();

        searchBoxCustomTextField.textProperty().addListener((observable, oldValue, newValue) -> onEnter());

        List<ReportUserDB> reportedUsersDB = new ReportHttpRequests().getAllManagedReportedUsers(Resources.getEmailHash());
        if(reportedUsersDB.size() == 0) {
            showEmptyReports();
            return;
        }
        else emptyDialogVBox.setManaged(false);

        sortedUsersMapByReportsNum = new LinkedHashMap<>(); //Preserva ordine di inserimento, utile per future rimozioni

        UserDB actualUser;
        sortedUsersMapByName = new LinkedHashMap<>();

        for(ReportUserDB reportedUserDB: reportedUsersDB) {
            actualUser = restTemplate.getForObject(getUserUrl, UserDB.class, reportedUserDB.getFKUtenteSegnalato());

            if(sortedUsersMapByReportsNum.get(actualUser) == null) {
                ArrayList<ReportUserDB> list1 = new ArrayList<>(), list2 = new ArrayList<>();
                list1.add(reportedUserDB);
                list2.add(reportedUserDB);
                sortedUsersMapByReportsNum.put(actualUser, list1); //Inizialmente le mappe non sono ordinate
                sortedUsersMapByName.put(actualUser, list2);
            }
            else {
                sortedUsersMapByReportsNum.get(actualUser).add(reportedUserDB);
                sortedUsersMapByName.get(actualUser).add(reportedUserDB);
            }
        }

        initializeMapByName();
        initializeMapByReportsNum();

        usersByNameRunnables = new ArrayList<>();
        ArrayList<Runnable> usersByReportsNumRunnables = new ArrayList<>();

        for(Map.Entry<UserDB, List<ReportUserDB>> entry: sortedUsersMapByReportsNum.entrySet())
            usersByReportsNumRunnables.add(getEventListenerForSelectedUser(entry.getKey(), entry.getValue()));

        for(Map.Entry<UserDB, List<ReportUserDB>> entry: sortedUsersMapByName.entrySet())
            usersByNameRunnables.add(getEventListenerForSelectedUser(entry.getKey(), entry.getValue()));

        usersByReportsNumGridPane = GridPaneGenerator.generateUsersGridPane(sortedUsersMapByReportsNum, usersByReportsNumRunnables);

        //Di default viene mostrato ordinamento decrescente per numero di segnalazioni
        containerScrollPane.setContent(usersByReportsNumGridPane);
        sortedByReportsNumber = true;
    }

    private Runnable getEventListenerForSelectedUser(UserDB user, List<ReportUserDB> reports) {
        return ()-> {
            ArrayList<UserDB> usersReporters = new ArrayList<>();

            for(ReportUserDB report: reports) {
                String reporterEmail = report.getFKUtenteSegnalatore();
                usersReporters.add(restTemplate.getForObject(getUserUrl, UserDB.class, reporterEmail));
            }

            try {
                new ReportUserContainerDetailsController().startManagedReports(user, usersReporters, homeController, this);
            }catch(IOException ignore) {}
        };
    }

    public List<ReportUserDB> getUserReportedUsers(UserDB reportedUser, String userEmail) {
        List<ReportUserDB> userMovieReports = new ArrayList<>();

        //Qualunque delle due mappe va bene
        for(ReportUserDB reportUserDB: sortedUsersMapByReportsNum.get(reportedUser))
            if(reportUserDB.getFKUtenteSegnalato().equals(reportedUser.getEmail()) && reportUserDB.getFKUtenteSegnalatore().equals(userEmail))
                userMovieReports.add(reportUserDB);

        return userMovieReports;
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

        Map<UserDB, List<ReportUserDB>> queryMap = new LinkedHashMap<>();
        ArrayList<Runnable> runnables = new ArrayList<>();

        /* Si sceglie di iterare su sortedMoviesMapByReportsNum tuttavia è indifferente
         * su quale delle due si itera, essendo i contenuti delle due mappe gli stessi */
        for(Map.Entry<UserDB, List<ReportUserDB>> entry: sortedUsersMapByReportsNum.entrySet()) {
            UserDB actualUser = entry.getKey();
            if(containsIgnoreCase(actualUser.getNome(), query)
                || containsIgnoreCase(actualUser.getCognome(), query)
                || containsIgnoreCase(actualUser.getUsername(), query)) {

                queryMap.put(entry.getKey(), entry.getValue());
                runnables.add(getEventListenerForSelectedUser(actualUser,  entry.getValue()));
            }
        }

        if(queryMap.size() > 0) {
            if(emptyDialogVBox.isVisible())
                hideEmptySearch();

            containerScrollPane.setContent(GridPaneGenerator.generateUsersGridPane(queryMap, runnables));
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
                    homeController.start(false);
                    stage.close();
                } catch (IOException ignore) {}
            }
        });

        iconSort.setOnMouseClicked(mouseEvent -> {
            if(sortedByReportsNumber) {
                if(usersByNameGridPane == null)
                    usersByNameGridPane = GridPaneGenerator.generateUsersGridPane(sortedUsersMapByName, usersByNameRunnables);
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
                .sorted(Comparator.comparing((Map.Entry<UserDB, List<ReportUserDB>> e1) -> e1.getKey().getNome())
                            .thenComparing((Map.Entry<UserDB, List<ReportUserDB>> e1) -> e1.getKey().getCognome()))
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

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void showAgain() {
        homeController.replaceHomeNode(managedReportUserContainerNode);
    }

    public void setManagedReportUsersContainerNode(Node managedReportUserContainerNode) {
        this.managedReportUserContainerNode = managedReportUserContainerNode;
    }
}