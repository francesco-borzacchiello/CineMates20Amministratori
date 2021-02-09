package it.unina.ingSw.cineMates20.model;


import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

public class ReportHttpRequests {

    private final RestTemplate restTemplate;
    private static final String DB_PATH = Resources.get(NameResources.DB_PATH);

    public ReportHttpRequests() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }

    public List<ReportMovieDB> getAllReportedMovies() {
        List<ReportMovieDB> reportedMovies = new LinkedList<>();

        Thread t = new Thread(()-> {
            try {
                String url = DB_PATH + "Report/getAllReportedMovies";

                ResponseEntity<List<ReportMovieDB>> responseReports = restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<ReportMovieDB>>() {});

                if(responseReports.getBody() != null && !responseReports.getBody().isEmpty())
                    reportedMovies.addAll(responseReports.getBody());

            }catch(HttpClientErrorException ignore){}
        });
        t.start();

        try {
            t.join();
        }catch(InterruptedException ignore){}

        return reportedMovies;
    }

    public List<ReportMovieDB> getAllManagedReportedMovies() {
        List<ReportMovieDB> reportedMovies = new LinkedList<>();

        Thread t = new Thread(()-> {
            try {
                String url = DB_PATH + "Report/getAllManagedReportedMovies";

                ResponseEntity<List<ReportMovieDB>> responseReports = restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<ReportMovieDB>>() {});

                if(responseReports.getBody() != null && !responseReports.getBody().isEmpty())
                    reportedMovies.addAll(responseReports.getBody());

            }catch(HttpClientErrorException ignore){}
        });
        t.start();

        try {
            t.join();
        }catch(InterruptedException ignore){}

        return reportedMovies;
    }

    public List<ReportUserDB> getAllReportedUsers() {
        List<ReportUserDB> reportedUsers = new LinkedList<>();

        Thread t = new Thread(()-> {
            try {
                String url = DB_PATH + "Report/getAllReportedUsers";

                ResponseEntity<List<ReportUserDB>> responseReports = restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<ReportUserDB>>() {});

                if(responseReports.getBody() != null && !responseReports.getBody().isEmpty())
                    reportedUsers.addAll(responseReports.getBody());

            }catch(HttpClientErrorException ignore){}
        });
        t.start();

        try {
            t.join();
        }catch(InterruptedException ignore){}

        return reportedUsers;
    }

    public List<ReportUserDB> getAllManagedReportedUsers() {
        List<ReportUserDB> reportedUsers = new LinkedList<>();

        Thread t = new Thread(()-> {
            try {
                String url = DB_PATH + "Report/getAllManagedReportedUsers";

                ResponseEntity<List<ReportUserDB>> responseReports = restTemplate.exchange(url, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<ReportUserDB>>() {});

                if(responseReports.getBody() != null && !responseReports.getBody().isEmpty())
                    reportedUsers.addAll(responseReports.getBody());

            }catch(HttpClientErrorException ignore){}
        });
        t.start();

        try {
            t.join();
        }catch(InterruptedException ignore){}

        return reportedUsers;
    }
}