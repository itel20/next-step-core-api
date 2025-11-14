package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.config.ApplicationProperties;
import com.nextstepsenegal.app.domain.AuthentificationByToken;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeycloakUserService {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakUserService.class);

    private final RestTemplate restTemplate;
    private final AuthentificationByToken authentificationByToken;
    private final ApplicationProperties applicationProperties;

    public KeycloakUserService(
        RestTemplate restTemplate,
        AuthentificationByToken authentificationByToken,
        ApplicationProperties applicationProperties
    ) {
        this.restTemplate = restTemplate;
        this.authentificationByToken = authentificationByToken;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Récupère tous les utilisateurs Keycloak
     *
     * @return Liste des utilisateurs
     */
    public List<Map<String, Object>> getAllUsers() {
        LOG.info("🔍 Récupération de tous les utilisateurs Keycloak...");

        String accessToken = getAccessToken();
        HttpHeaders headers = createHeaders(accessToken);

        String url = applicationProperties.getKcUrl1(); // URL: /admin/realms/{realm}/users

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> users = response.getBody();
            LOG.info("✅ {} utilisateurs trouvés", users != null ? users.size() : 0);
            return users;
        } catch (Exception e) {
            LOG.error("Erreur lors de la récupération des utilisateurs", e);
            throw new RuntimeException("Erreur récupération utilisateurs: " + e.getMessage());
        }
    }

    /**
     * Récupère un utilisateur par son email
     *
     * @param email Email de l'utilisateur
     * @return Informations de l'utilisateur ou null
     */
    public Map<String, Object> getUserByEmail(String email) {
        LOG.info("🔍 Recherche utilisateur Keycloak: {}", email);

        String accessToken = getAccessToken();
        HttpHeaders headers = createHeaders(accessToken);

        String url = applicationProperties.getKcUrl1() + "?email=" + email;

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> users = response.getBody();

            if (users != null && !users.isEmpty()) {
                Map<String, Object> user = users.get(0);
                LOG.info("✅ Utilisateur trouvé: {} (ID: {})", email, user.get("id"));
                return user;
            } else {
                LOG.warn("⚠️ Aucun utilisateur trouvé avec l'email: {}", email);
                return null;
            }
        } catch (Exception e) {
            LOG.error("❌ Erreur lors de la recherche de l'utilisateur", e);
            throw new RuntimeException("Erreur recherche utilisateur: " + e.getMessage());
        }
    }

    /**
     * Récupère un utilisateur par son ID Keycloak
     *
     * @param userId ID Keycloak de l'utilisateur
     * @return Informations de l'utilisateur
     */
    public Map<String, Object> getUserById(String userId) {
        LOG.info("🔍 Récupération utilisateur Keycloak ID: {}", userId);

        String accessToken = getAccessToken();
        HttpHeaders headers = createHeaders(accessToken);

        String url = applicationProperties.getKcUrl1() + "/" + userId;

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> user = response.getBody();
            LOG.info("✅ Utilisateur trouvé: {} {}", user.get("firstName"), user.get("lastName"));
            return user;
        } catch (Exception e) {
            LOG.error("❌ Erreur lors de la récupération de l'utilisateur", e);
            throw new RuntimeException("Erreur récupération utilisateur: " + e.getMessage());
        }
    }

    /**
     * Récupère les rôles d'un utilisateur
     *
     * @param userId ID Keycloak de l'utilisateur
     * @return Liste des rôles
     */
    public List<Map<String, Object>> getUserRoles(String userId) {
        LOG.info("🔍 Récupération des rôles pour l'utilisateur: {}", userId);

        String accessToken = getAccessToken();
        HttpHeaders headers = createHeaders(accessToken);

        String baseUrl = applicationProperties.getKcBaseUrl();
        String realm = applicationProperties.getKcRealm();
        String url = baseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> roles = response.getBody();
            LOG.info("✅ {} rôles trouvés", roles != null ? roles.size() : 0);
            return roles;
        } catch (Exception e) {
            LOG.error("❌ Erreur lors de la récupération des rôles", e);
            throw new RuntimeException("Erreur récupération rôles: " + e.getMessage());
        }
    }

    /**
     * Affiche les détails complets d'un utilisateur (avec rôles)
     *
     * @param email Email de l'utilisateur
     * @return Détails complets de l'utilisateur
     */
    public Map<String, Object> getUserFullDetails(String email) {
        LOG.info("📋 Récupération détails complets pour: {}", email);

        Map<String, Object> user = getUserByEmail(email);

        if (user == null) {
            return null;
        }

        String userId = (String) user.get("id");
        List<Map<String, Object>> roles = getUserRoles(userId);

        Map<String, Object> fullDetails = new HashMap<>(user);
        fullDetails.put("roles", roles);

        LOG.info("✅ Détails complets récupérés pour: {}", email);
        return fullDetails;
    }

    /**
     * Compte le nombre total d'utilisateurs
     *
     * @return Nombre d'utilisateurs
     */
    public int getUserCount() {
        LOG.info("🔢 Comptage des utilisateurs...");

        String accessToken = getAccessToken();
        HttpHeaders headers = createHeaders(accessToken);

        String baseUrl = applicationProperties.getKcBaseUrl();
        String realm = applicationProperties.getKcRealm();
        String url = baseUrl + "/admin/realms/" + realm + "/users/count";

        try {
            ResponseEntity<Integer> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Integer.class);

            Integer count = response.getBody();
            LOG.info("✅ Nombre d'utilisateurs: {}", count);
            return count != null ? count : 0;
        } catch (Exception e) {
            LOG.error("❌ Erreur lors du comptage des utilisateurs", e);
            throw new RuntimeException("Erreur comptage utilisateurs: " + e.getMessage());
        }
    }

    // === MÉTHODES UTILITAIRES ===

    private String getAccessToken() {
        String accessToken = authentificationByToken.authentificationFonction(
            applicationProperties.getKcUser(),
            applicationProperties.getKcPassword()
        );

        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Échec d'authentification Keycloak");
        }

        return accessToken;
    }

    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        return headers;
    }
}
