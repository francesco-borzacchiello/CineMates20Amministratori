package it.unina.ingSw.cineMates20.controller;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.*;
import info.movito.themoviedbapi.model.people.PersonCrew;

import it.unina.ingSw.cineMates20.App;
import it.unina.ingSw.cineMates20.model.ReportHttpRequests;
import it.unina.ingSw.cineMates20.model.ReportMovieDB;
import it.unina.ingSw.cineMates20.model.UserDB;
import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import it.unina.ingSw.cineMates20.view.GridPaneGenerator;
import it.unina.ingSw.cineMates20.view.MessageDialog;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.IOException;
import java.util.*;
import java.util.regex.PatternSyntaxException;

public class ReportMoviesContainerDetailsController extends Controller {

    @FXML
    private Label titolo,
                  descrizioneFilm,
                  iconBack,
                  detailsIconBack,
                  motivoSegnalazione;

    @FXML
    private TextFlow dataDiUscitaTextFlow,
                     registaTextFlow,
                     genereTextFlow,
                     durataTextFlow,
                     etaConsigliataTextFlow,
                     numeroSegnalazioniTextFlow;

    @FXML
    private ImageView movieImageView;

    @FXML
    private ScrollPane reportersContainerScrollPane;

    @FXML
    private VBox reportersVBox,
                 reportDetailsVBox;

    @FXML
    private Button approvaButton,
                   rigettaButton,
                   oscuraButton;

    @FXML
    private GridPane reporterDetailsGridPane;

    private static MovieDb reportedMovie;
    private static List<UserDB> reporters;

    private static List<ReportMovieDB> userMovieReports; //Segnalazioni di questo film dello stesso utente

    private static ReportMoviesContainerController reportMoviesContainerController;
    private static ManagedReportsMoviesContainerController managedReportsMoviesContainerController;
    private static ReportHttpRequests reportHttpRequests;

    private static boolean actualReportTypeIsPending;

    @FXML
    @Override
    protected void initialize() {
        initializeIcons();
        setUpIconListeners();

        initializeReportedMovie();

        //Raggruppamento segnalazioni per utenti
        reporters.sort(Comparator.comparing(UserDB::getNome)
                      .thenComparing(UserDB::getCognome)
                      .thenComparing(UserDB::getUsername)
                      .thenComparing(UserDB::getEmail));

        initializeReportersGridPane();
        initializeVBoxes();

        reportHttpRequests = new ReportHttpRequests();
    }

    private void initializeReportersGridPane() {
        ArrayList<Runnable> listeners = new ArrayList<>();
        UserDB actualReporter = reporters.get(0);
        initializeReportReasons(actualReporter); //Inizializza la lista di segnalazioni di questo utente per questo film
        for(UserDB reporter: reporters) {
            //Se l'utente è cambiato...
            if(!actualReporter.equals(reporter)) {
                //Si aggiorna la lista di segnalazioni di questo film di questo utente
                initializeReportReasons(reporter);
                //L'utente attuale si aggiorna
                actualReporter = reporter;
            }
            //Nota: userMovieReports.size() corrisponde al numero di segnalazioni di questo film di questo utente (segnalazioni duplicate)
            listeners.add(getEventListenerForReporterUser(reporter, userMovieReports.remove(0)));
        }

        GridPane reportersGridPane = GridPaneGenerator.generateMovieReportersUsersGridPane(reporters, listeners);

        reportersContainerScrollPane.setContent(reportersGridPane);
    }

    private void initializeVBoxes() {
        reportDetailsVBox.setVisible(false);
        reportDetailsVBox.managedProperty().bind(reportDetailsVBox.visibleProperty());

        reportersVBox.managedProperty().bind(reportersVBox.visibleProperty());
    }

    private Runnable getEventListenerForReporterUser(UserDB reporter, ReportMovieDB report) {
        return () ->
            Platform.runLater(() -> {
                if(actualReportTypeIsPending) {
                    approvaButton.setOnMouseClicked(getEventListenerForReportButton(reporter, report, "Approvata"));
                    rigettaButton.setOnMouseClicked(getEventListenerForReportButton(reporter, report, "Rigettata"));
                    oscuraButton.setOnMouseClicked(getEventListenerForReportButton(reporter, report, "Oscurata"));
                }
                else {
                    disableAllReportButtons(true);

                    switch(report.getEsitoSegnalazione()) {
                        case "Rigettata": {
                            rigettaButton.setDisable(false);
                            rigettaButton.setMouseTransparent(true);
                            break;
                        }
                        case "Approvata": {
                            approvaButton.setDisable(false);
                            approvaButton.setMouseTransparent(true);
                            break;
                        }
                        case "Oscurata": {
                            oscuraButton.setDisable(false);
                            oscuraButton.setMouseTransparent(true);
                            break;
                        }
                    }
                }

                ArrayList<UserDB> reporterArrayList = new ArrayList<>();
                reporterArrayList.add(reporter);
                ArrayList<Runnable> emptyRunnables = new ArrayList<>();
                emptyRunnables.add(()-> {});

                reporterDetailsGridPane.getChildren().add(GridPaneGenerator.generateMovieReportersUsersGridPane(reporterArrayList, emptyRunnables));

                reportDetailsVBox.setVisible(true);
                reportersVBox.setVisible(false);

                motivoSegnalazione.setText(report.getMessaggioSegnalazione());
            });
    }

    private EventHandler<? super MouseEvent> getEventListenerForReportButton(UserDB reporter, ReportMovieDB report, String reportOutcome) {
        return (mouseEvent -> {
            disableAllReportButtons(true);
            report.setEsitoSegnalazione(reportOutcome);
            String emailHash = BCrypt.hashpw(Objects.requireNonNull(Resources.getEmail()), BCrypt.gensalt());
            if(!reportHttpRequests.adminUpdateMovieReport(report, emailHash))
                MessageDialog.error("Si è verificato un errore", "Si è verificato un errore, riprova più tardi.");

            if(reportOutcome.equals("Oscurata"))
                detailsIconBack.setOnMouseClicked((mouseEvent2 -> {
                    if (actualReportTypeIsPending)
                        reportMoviesContainerController.showAgain();
                    else
                        managedReportsMoviesContainerController.showAgain();
                }));

            deleteSelectedUserFromReportersGridPane(reporter);

            switch(report.getEsitoSegnalazione()) {
                case "Rigettata": {
                    rigettaButton.setDisable(false);
                    rigettaButton.setMouseTransparent(true);
                    rigettaButton.setOnMouseClicked((mouseEvent2)->{});
                    break;
                }
                case "Approvata": {
                    approvaButton.setDisable(false);
                    approvaButton.setMouseTransparent(true);
                    approvaButton.setOnMouseClicked((mouseEvent2)->{});
                    break;
                }
                case "Oscurata": {
                    oscuraButton.setDisable(false);
                    oscuraButton.setMouseTransparent(true);
                    oscuraButton.setOnMouseClicked((mouseEvent2)->{});
                    break;
                }
            }
        });
    }

    private void deleteSelectedUserFromReportersGridPane(UserDB reporter) {
        if(actualReportTypeIsPending && reportMoviesContainerController != null)
            reportMoviesContainerController.updateReportsLayout();

        reporters.removeAll(Collections.singleton(reporter));

        numeroSegnalazioniTextFlow.getChildren().remove(1);
        Text numSegnalazioni = new Text("" + reporters.size());
        numSegnalazioni.setStyle("-fx-font-size: 16.0");
        numSegnalazioni.setFill(Paint.valueOf("white"));
        numeroSegnalazioniTextFlow.getChildren().add(numSegnalazioni);

        if(reporters.size() > 0)
            initializeReportersGridPane();
        else
            detailsIconBack.setOnMouseClicked((mouseEvent -> {
                if(actualReportTypeIsPending)
                    reportMoviesContainerController.showAgain();
                else
                    managedReportsMoviesContainerController.showAgain();
            }));
    }

    private void disableAllReportButtons(boolean disable) {
        approvaButton.setDisable(disable);
        rigettaButton.setDisable(disable);
        oscuraButton.setDisable(disable);
    }

    private void resetAllReportButtons() {
        disableAllReportButtons(false);
        approvaButton.setMouseTransparent(false);
        rigettaButton.setMouseTransparent(false);
        oscuraButton.setMouseTransparent(false);
    }

    //Inizializza le segnalazioni di questo utente (reporter) per il film attualmente selezionato (reportedMovie)
    private void initializeReportReasons(UserDB reporter) {
        if(userMovieReports != null)
            userMovieReports.clear();

        if(actualReportTypeIsPending && reportMoviesContainerController != null) {
            userMovieReports = reportMoviesContainerController.getUserMovieReports(reportedMovie, reporter.getEmail());
        }
        else if(!actualReportTypeIsPending && managedReportsMoviesContainerController != null) {
            userMovieReports = managedReportsMoviesContainerController.getUserMovieReports(reportedMovie, reporter.getEmail());
        }
    }

    public void startPendingReports(MovieDb reportedMovie, List<UserDB> reporters, HomeController homeController,
                                    ReportMoviesContainerController reportMoviesContainerController) throws IOException {
        ReportMoviesContainerDetailsController.reportedMovie = reportedMovie;
        ReportMoviesContainerDetailsController.reporters = reporters;
        ReportMoviesContainerDetailsController.reportMoviesContainerController = reportMoviesContainerController; //Necessario per riferire eliminazione segnalazione
        actualReportTypeIsPending = true;

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Resources.get(NameResources.DIRECTORY_FXML) + "/" +
                Resources.get(NameResources.REPORT_MOVIES_CONTAINER_DETAILS_LAYOUT) + ".fxml"));

        homeController.replaceHomeNode(fxmlLoader.load());
    }

    public void startManagedReports(MovieDb reportedMovie, List<UserDB> reporters, HomeController homeController,
                                    ManagedReportsMoviesContainerController managedReportsMoviesContainerController) throws IOException {
        ReportMoviesContainerDetailsController.reportedMovie = reportedMovie;
        ReportMoviesContainerDetailsController.reporters = reporters;
        ReportMoviesContainerDetailsController.managedReportsMoviesContainerController = managedReportsMoviesContainerController; //Necessario per riferire eliminazione segnalazione
        actualReportTypeIsPending = false;

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(Resources.get(NameResources.DIRECTORY_FXML) + "/" +
                Resources.get(NameResources.REPORT_MOVIES_CONTAINER_DETAILS_LAYOUT) + ".fxml"));

        homeController.replaceHomeNode(fxmlLoader.load());
    }

    private void initializeReportedMovie() {
        String IMAGE_FIRST_PATH = Resources.get(NameResources.FIRST_PATH_IMAGE_ORIGINAL);

        Platform.runLater(()-> {
            if(reportedMovie.getTitle() != null)
                titolo.setText(reportedMovie.getTitle());
            else
                titolo.setText(reportedMovie.getOriginalTitle());

            initializeTextFlows();

            if(reportedMovie.getOverview() != null && !reportedMovie.getOverview().equals(""))
                descrizioneFilm.setText(reportedMovie.getOverview());
            else
                descrizioneFilm.setText("Non disponibile");

            if(reportedMovie.getPosterPath() != null)
                new Thread(()-> {
                    Image image = new Image(IMAGE_FIRST_PATH + reportedMovie.getPosterPath(), 224, 320, false, false);
                    Platform.runLater(()-> movieImageView.setImage(image));
                }).start();

            Rectangle rectangle = new Rectangle(movieImageView.getFitWidth(), movieImageView.getFitHeight());
            rectangle.setArcHeight(20.0);
            rectangle.setArcWidth(20.0);
            movieImageView.setClip(rectangle);
        });
    }

    private void initializeTextFlows() {
        TmdbMovies tmdbMovies = new TmdbApi(Resources.getTmdbApiKey()).getMovies();

        Text dataDiUscita = new Text(getEuropeanFormatMovieReleaseDate(reportedMovie, tmdbMovies));
        dataDiUscita.setStyle("-fx-font-size: 15.0");
        dataDiUscita.setFill(Paint.valueOf("white"));
        dataDiUscitaTextFlow.getChildren().add(dataDiUscita);

        Text regista = new Text(getDirectorByMovieId(reportedMovie.getId(), tmdbMovies));
        regista.setStyle("-fx-font-size: 15.0");
        regista.setFill(Paint.valueOf("white"));
        registaTextFlow.getChildren().add(regista);

        Text genere = new Text(getMovieGenresById(reportedMovie.getId(), tmdbMovies));
        genere.setStyle("-fx-font-size: 15.0");
        genere.setFill(Paint.valueOf("white"));
        genereTextFlow.getChildren().add(genere);

        Text durata = new Text(getMovieRuntimeById(reportedMovie.getId(), tmdbMovies));
        durata.setStyle("-fx-font-size: 15.0");
        durata.setFill(Paint.valueOf("white"));
        durataTextFlow.getChildren().add(durata);

        Text etaConsigliata = new Text(getAverageRecommendedAgeById(reportedMovie.getId(), tmdbMovies));
        etaConsigliata.setStyle("-fx-font-size: 15.0");
        etaConsigliata.setFill(Paint.valueOf("white"));
        etaConsigliataTextFlow.getChildren().add(etaConsigliata);

        Text numSegnalazioni = new Text("" + reporters.size());
        numSegnalazioni.setStyle("-fx-font-size: 16.0");
        numSegnalazioni.setFill(Paint.valueOf("white"));
        numeroSegnalazioniTextFlow.getChildren().add(numSegnalazioni);
    }

    /* Restituisce la data di uscita italiana di un film in formato europeo,
       nel caso non disponibile prova a restituire la data di uscita americana */
    private String getEuropeanFormatMovieReleaseDate(@NotNull MovieDb movie, TmdbMovies tmdbMovies) {
        String movieReleaseDate = "Non disponibile",
               usFormatItalianReleaseDate = null,
               usReleaseDate = null;

        //Recupero data di uscita in italia
        for(ReleaseInfo releaseInfo: tmdbMovies.getReleaseInfo(movie.getId(),"it")){
            if(releaseInfo.getCountry() != null &&
                    (releaseInfo.getCountry().equals("IT") || releaseInfo.getCountry().equals("it"))) {
                for(ReleaseDate releaseDate: releaseInfo.getReleaseDates()) {
                    usFormatItalianReleaseDate = releaseDate.getReleaseDate().subSequence(0,10).toString();
                }
            }
            else if(releaseInfo.getCountry() != null && releaseInfo.getCountry().equals("US")) {
                for(ReleaseDate releaseDate: releaseInfo.getReleaseDates()) {
                    usReleaseDate = releaseDate.getReleaseDate().subSequence(0,10).toString();
                }
            }
        }

        //Conversione formato data da americano a europeo
        if(usFormatItalianReleaseDate != null && !usFormatItalianReleaseDate.equals("")) {
            String[] arr = usFormatItalianReleaseDate.split("-");
            movieReleaseDate = arr[2] + "-" + arr[1] + "-" + arr[0];
        }
        else if(usReleaseDate != null && !usReleaseDate.equals("")) {
            String[] arr = usReleaseDate.split("-");
            movieReleaseDate = arr[2] + "-" + arr[1] + "-" + arr[0] + " [USA]";
        }

        return movieReleaseDate;
    }

    private String getDirectorByMovieId(int id, TmdbMovies tmdbMovies) {
        String director = "Non disponibile";

        Credits credits = tmdbMovies.getCredits(id);
        if(credits != null)
            for(PersonCrew person : credits.getCrew()) {
                if(person.getJob().equals("Director"))
                    director = person.getName();
            }

        return director;
    }

    private String getMovieGenresById(int id, TmdbMovies tmdbMovies) {
        String trimmedGenres = "Non disponibile";

        List<Genre> genres = tmdbMovies.getMovie(id, "it").getGenres();
        if(genres == null || genres.size() == 0)
            return trimmedGenres;

        try {
            trimmedGenres = genres.toString().replaceAll("\\[\\d+]|\\[|]", "");
            trimmedGenres = trimmedGenres.replaceAll(" ,", ",");
        }catch(PatternSyntaxException ignore) {}

        return trimmedGenres;
    }

    private String getMovieRuntimeById(int id, TmdbMovies tmdbMovies) {
        String runtime = "Non disponibile";

        int time = tmdbMovies.getMovie(id,"it").getRuntime();
        if(time > 0)
            runtime = time + "m";

        return runtime;
    }

    private String getAverageRecommendedAgeById(int id, TmdbMovies tmdbMovies) {
        String certification = "Non disponibile";

        List<ReleaseInfo> releaseInfo = tmdbMovies.getReleaseInfo(id, "it");
        int size = 0, sum = 0;

        for(ReleaseInfo info: releaseInfo) {
            List<ReleaseDate> releaseDate = info.getReleaseDates();
            for(ReleaseDate rd: releaseDate) {
                Scanner in = new Scanner(rd.getCertification()).useDelimiter("[^0-9]+");
                try{
                    sum += in.nextInt();
                    size++;
                }catch(NoSuchElementException ignore){}
            }
        }

        if(size > 0 && (sum/size) > 0)
            certification = "" +  sum/size;

        return certification;
    }

    private void setUpIconListeners() {
        iconBack.setOnMouseClicked(mouseEvent -> {
            if(actualReportTypeIsPending)
                reportMoviesContainerController.showAgain();
            else
                managedReportsMoviesContainerController.showAgain();
        });

        detailsIconBack.setOnMouseClicked(mouseEvent -> {
            reportDetailsVBox.setVisible(false);
            reporterDetailsGridPane.getChildren().clear();
            reportersVBox.setVisible(true);

            resetAllReportButtons();
        });
    }

    private void initializeIcons() {
        Text icon = GlyphsDude.createIcon(FontAwesomeIcon.ARROW_LEFT, "4em");
        icon.setFill(Paint.valueOf("white"));
        iconBack.setGraphic(icon);

        Text icon2 = GlyphsDude.createIcon(FontAwesomeIcon.ARROW_LEFT, "4em");
        icon2.setFill(Paint.valueOf("white"));
        detailsIconBack.setGraphic(icon2);
    }
}