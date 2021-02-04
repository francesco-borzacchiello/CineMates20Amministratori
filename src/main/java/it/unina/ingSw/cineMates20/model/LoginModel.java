package it.unina.ingSw.cineMates20.model;

import it.unina.ingSw.cineMates20.utils.NameResources;
import it.unina.ingSw.cineMates20.utils.Resources;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class LoginModel {

    private final RestTemplate restTemplate;
    private final String DB_PATH,
                         ADMIN_PATH,
                         GET_PSW_HASH_PATH;

    public LoginModel() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        DB_PATH = Resources.get(NameResources.DB_PATH);
        ADMIN_PATH = Resources.get(NameResources.ADMIN_PATH);
        GET_PSW_HASH_PATH = Resources.get(NameResources.GET_PSW_HASH_PATH);
    }

    public boolean login(String email, String password) {
        String pswHash = getPswHash(email);
        if(pswHash != null)
            return BCrypt.checkpw(password, pswHash);
        return false;
    }

    private String getPswHash(String email) {
        String url = DB_PATH + ADMIN_PATH + GET_PSW_HASH_PATH;
        String emailHash = BCrypt.hashpw(email, BCrypt.gensalt());

        String pswHash = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> requestEntity = new HttpEntity<>(emailHash, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            if(responseEntity.getStatusCode() == HttpStatus.OK)
                pswHash = responseEntity.getBody();
        }catch(HttpClientErrorException ignore) {}

        return pswHash;
    }
}