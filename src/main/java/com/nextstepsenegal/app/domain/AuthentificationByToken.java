package com.nextstepsenegal.app.domain;

import com.nextstepsenegal.app.config.ApplicationProperties;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthentificationByToken {

    public final ApplicationProperties applicationProperties;

    public final RestTemplate restTemplate;

    public AuthentificationByToken(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
        this.applicationProperties = applicationProperties;
        this.restTemplate = restTemplate;
    }

    public String authentificationFonction(String user, String password) {
        //Todo: sortir l'url dans les parametres
        user = applicationProperties.getKcUser();
        password = applicationProperties.getKcPassword();
        RestTemplate restTemplate = new RestTemplate();
        String url = applicationProperties.getKcUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", user);
        map.add("password", password);
        map.add("client_id", applicationProperties.getKcClientId());
        map.add("client_secret", applicationProperties.getKcClientSecret());
        map.add("grant_type", "password");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, String> tokenData = response.getBody();
            assert tokenData != null;
            return tokenData.get("access_token");
        } else {
            throw new RuntimeException("echec lors de l'authentification");
        }
    }
}
