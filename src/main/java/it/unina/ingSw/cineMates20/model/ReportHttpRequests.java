package it.unina.ingSw.cineMates20.model;

import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReportHttpRequests {

    private final RestTemplate restTemplate;
    private static final String DB_PATH = Resources.get(NameResources.DB_PATH),
                                REPORT_PATH = Resources.get(NameResources.REPORT_PATH),
                                UPDATE_MOVIE_REPORT_PATH = Resources.get(NameResources.UPDATE_MOVIE_REPORT_PATH);

    public ReportHttpRequests() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }

    public List<ReportMovieDB> getAllReportedMovies() {
        List<ReportMovieDB> reportedMovies = new LinkedList<>();

        try {
            String url = DB_PATH + "Report/getAllReportedMovies";

            ResponseEntity<List<ReportMovieDB>> responseReports = restTemplate.exchange(url, HttpMethod.GET,
                   null, new ParameterizedTypeReference<List<ReportMovieDB>>() {});

            if(responseReports.getBody() != null && !responseReports.getBody().isEmpty())
                reportedMovies.addAll(responseReports.getBody());

        }catch(HttpClientErrorException ignore){}

        return reportedMovies;
    }

    public List<ReportMovieDB> getAllManagedReportedMovies(String emailHash) {
        List<ReportMovieDB> reportedMovies = new LinkedList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> requestEntity = new HttpEntity<>(emailHash, headers);

        try {
            String url = DB_PATH + "Report/getAllManagedReportedMovies";

            ResponseEntity<List<ReportMovieDB>> responseReports =
                    restTemplate.exchange(url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<ReportMovieDB>>() {});

            if(responseReports.getBody() != null && !responseReports.getBody().isEmpty())
                reportedMovies.addAll(responseReports.getBody());

        }catch(HttpClientErrorException ignore){}

        return reportedMovies;
    }

    public List<ReportUserDB> getAllReportedUsers() {
        List<ReportUserDB> reportedUsers = new LinkedList<>();

        try {
            String url = DB_PATH + "Report/getAllReportedUsers";

            ResponseEntity<List<ReportUserDB>> responseReports = restTemplate.exchange(url, HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<ReportUserDB>>() {});

            if(responseReports.getBody() != null && !responseReports.getBody().isEmpty())
                reportedUsers.addAll(responseReports.getBody());

        }catch(HttpClientErrorException ignore){}

        return reportedUsers;
    }

    public List<ReportUserDB> getAllManagedReportedUsers(String emailHash) {
        List<ReportUserDB> reportedUsers = new LinkedList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> requestEntity = new HttpEntity<>(emailHash, headers);

        try {
            String url = DB_PATH + "Report/getAllManagedReportedUsers";

            ResponseEntity<List<ReportUserDB>> responseReports = restTemplate.exchange(url, HttpMethod.POST,
                    requestEntity, new ParameterizedTypeReference<List<ReportUserDB>>() {});

            if(responseReports.getBody() != null && !responseReports.getBody().isEmpty())
                reportedUsers.addAll(responseReports.getBody());

        }catch(HttpClientErrorException ignore){}

        return reportedUsers;
    }

    public boolean adminUpdateMovieReport(ReportMovieDB report, String emailHash) {
        String url = DB_PATH + REPORT_PATH + UPDATE_MOVIE_REPORT_PATH;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HashMap<String, ReportMovieDB> map = new HashMap<>();
        map.put(emailHash, report);
        HttpEntity<Map<String, ReportMovieDB>> requestEntity = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Boolean> responseEntity = restTemplate.postForEntity(url, requestEntity, Boolean.class);

            if(responseEntity.getBody() != null && responseEntity.getStatusCode() == HttpStatus.OK)
                return responseEntity.getBody();
        }catch(HttpClientErrorException ignore) {}

        return false;
    }
}