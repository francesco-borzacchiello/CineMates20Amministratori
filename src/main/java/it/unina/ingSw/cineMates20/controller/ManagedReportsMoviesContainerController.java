package it.unina.ingSw.cineMates20.controller;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

import it.unina.ingSw.cineMates20.model.ReportHttpRequests;
import it.unina.ingSw.cineMates20.model.ReportMovieDB;
import it.unina.ingSw.cineMates20.model.UserDB;
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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class ManagedReportsMoviesContainerController extends Controller{

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

    private Map<MovieDb, List<ReportMovieDB>> sortedMoviesMapByTitles,
                                              sortedMoviesMapByReportsNum;

    private GridPane moviesByTitlesGridPane,
                     moviesByReportsNumGridPane;

    private boolean sortedByReportsNumber;

    private HomeController homeController;
    private Node managedReportsMoviesContainerNode;

    private final RestTemplate restTemplate;
    private final String getUserUrl;

    private ArrayList<Runnable> moviesByTitlesRunnables;

    public ManagedReportsMoviesContainerController() {
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

        String emailHash = BCrypt.hashpw(Objects.requireNonNull(Resources.getEmail()), BCrypt.gensalt());
        List<ReportMovieDB> reportedMoviesDB = new ReportHttpRequests().getAllManagedReportedMovies(emailHash);
        if(reportedMoviesDB.size() == 0){
            showEmptyReports();
            return;
        }
        else emptyDialogVBox.setManaged(false);

        sortedMoviesMapByReportsNum = new LinkedHashMap<>(); //Preserva ordine di inserimento, utile per future rimozioni

        MovieDb actualMovie;
        TmdbMovies tmdbMovies = new TmdbApi(Resources.getTmdbApiKey()).getMovies();
        sortedMoviesMapByTitles = new LinkedHashMap<>();

        for(ReportMovieDB reportedMovieDB: reportedMoviesDB) {
            actualMovie = tmdbMovies.getMovie(reportedMovieDB.getFKFilmSegnalato().intValue(), "it");

            if(sortedMoviesMapByReportsNum.get(actualMovie) == null) {
                ArrayList<ReportMovieDB> list1 = new ArrayList<>(), list2 = new ArrayList<>();
                list1.add(reportedMovieDB);
                list2.add(reportedMovieDB);
                sortedMoviesMapByReportsNum.put(actualMovie, list1); //Inizialmente le mappe non sono ordinate
                sortedMoviesMapByTitles.put(actualMovie, list2);
            }
            else {
                sortedMoviesMapByReportsNum.get(actualMovie).add(reportedMovieDB);
                sortedMoviesMapByTitles.get(actualMovie).add(reportedMovieDB);
            }
        }

        initializeMapByTitles();
        initializeMapByReportsNum();

        moviesByTitlesRunnables = new ArrayList<>();
        ArrayList<Runnable> moviesByReportsNumRunnables = new ArrayList<>();

        for(Map.Entry<MovieDb, List<ReportMovieDB>> entry: sortedMoviesMapByReportsNum.entrySet())
            moviesByReportsNumRunnables.add(getEventListenerForSelectedMovie(entry.getKey(), entry.getValue()));

        for(Map.Entry<MovieDb, List<ReportMovieDB>> entry: sortedMoviesMapByTitles.entrySet())
            moviesByTitlesRunnables.add(getEventListenerForSelectedMovie(entry.getKey(), entry.getValue()));

        moviesByReportsNumGridPane = GridPaneGenerator.generateMoviesGridPane(sortedMoviesMapByReportsNum, moviesByReportsNumRunnables);

        //Di default viene mostrato ordinamento decrescente per numero di segnalazioni
        containerScrollPane.setContent(moviesByReportsNumGridPane);
        sortedByReportsNumber = true;
    }

    private Runnable getEventListenerForSelectedMovie(MovieDb movie, List<ReportMovieDB> reporters) {
        return ()-> {
            ArrayList<UserDB> usersReporters = new ArrayList<>();

            for(ReportMovieDB reporter: reporters) {
                String reporterEmail = reporter.getFKUtenteSegnalatore();
                usersReporters.add(restTemplate.getForObject(getUserUrl, UserDB.class, reporterEmail));
            }

            try {
                new ReportMoviesContainerDetailsController().startManagedReports(movie, usersReporters, homeController, this);
            }catch(IOException ignore) {}
        };
    }

    public List<ReportMovieDB> getUserMovieReports(MovieDb movie, String userEmail) {
        List<ReportMovieDB> userMovieReports = new ArrayList<>();

        //Qualunque delle due mappe va bene
        for(ReportMovieDB reportMovieDB: sortedMoviesMapByReportsNum.get(movie))
            if(reportMovieDB.getFKFilmSegnalato() == movie.getId() && reportMovieDB.getFKUtenteSegnalatore().equals(userEmail))
                userMovieReports.add(reportMovieDB);

        return userMovieReports;
    }

    @FXML
    private void onEnter(){
        String query = searchBoxCustomTextField.getText().trim();

        if(query.equals("")) {
            if(sortedByReportsNumber)
                containerScrollPane.setContent(moviesByReportsNumGridPane);
            else
                containerScrollPane.setContent(moviesByTitlesGridPane);

            if(emptyDialogVBox.isVisible())
                hideEmptySearch();

            iconSort.setVisible(true);

            return;
        }

        iconSort.setVisible(false);

        Map<MovieDb, List<ReportMovieDB>> queryMap = new LinkedHashMap<>();
        ArrayList<Runnable> runnables = new ArrayList<>();

        /* Si sceglie di iterare su sortedMoviesMapByReportsNum tuttavia è indifferente
         * su quale delle due si itera, essendo i contenuti delle due mappe gli stessi */
        for(Map.Entry<MovieDb, List<ReportMovieDB>> entry: sortedMoviesMapByReportsNum.entrySet()) {
            MovieDb actualMovie = entry.getKey();
            if((actualMovie.getTitle() != null && containsIgnoreCase(actualMovie.getTitle(), query))
               || (containsIgnoreCase(actualMovie.getOriginalTitle(), query))) {

                queryMap.put(entry.getKey(), entry.getValue());
                runnables.add(getEventListenerForSelectedMovie(actualMovie, entry.getValue()));
            }
        }

        if(queryMap.size() > 0) {
            if(emptyDialogVBox.isVisible())
                hideEmptySearch();

            containerScrollPane.setContent(GridPaneGenerator.generateMoviesGridPane(queryMap, runnables));
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
                    new HomeController().start(false);
                    stage.close();
                } catch (IOException ignore) {}
            }
        });

        iconSort.setOnMouseClicked(mouseEvent -> {
            if(sortedByReportsNumber) {
                if(moviesByTitlesGridPane == null)
                    moviesByTitlesGridPane = GridPaneGenerator.generateMoviesGridPane(sortedMoviesMapByTitles, moviesByTitlesRunnables);
                containerScrollPane.setContent(moviesByTitlesGridPane);
                sortedByReportsNumber = false;
            }
            else {
                containerScrollPane.setContent(moviesByReportsNumGridPane);
                sortedByReportsNumber = true;
            }
        });
    }

    //Metodo per ordinare i film in base alle segnalazioni
    private void initializeMapByReportsNum() {
        sortedMoviesMapByReportsNum = sortedMoviesMapByReportsNum.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> -e.getValue().size()))
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> { throw new AssertionError(); },
                        LinkedHashMap::new
                ));
    }

    //Metodo per ordinare i film in base ai titoli
    private void initializeMapByTitles() {
        sortedMoviesMapByTitles = sortedMoviesMapByTitles.entrySet().stream()
                .sorted((e1, e2) -> {
                    MovieDb movie1 = e1.getKey(),
                            movie2 = e2.getKey();
                    if (movie1.getTitle() != null && movie2.getTitle() != null)
                        return movie1.getTitle().compareTo(movie2.getTitle());
                    return movie1.getOriginalTitle().compareTo(movie2.getOriginalTitle());
                })
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
        homeController.replaceHomeNode(managedReportsMoviesContainerNode);
    }

    public void setManagedReportsMoviesContainerNode(Node managedReportsMoviesContainerNode) {
        this.managedReportsMoviesContainerNode = managedReportsMoviesContainerNode;
    }
}