package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.service.KeycloakUserService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller pour gérer les utilisateurs Keycloak
 */
@RestController
@RequestMapping("/api/keycloak")
public class KeycloakUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(KeycloakUserResource.class);

    private final KeycloakUserService keycloakUserService;

    public KeycloakUserResource(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    /**
     * GET /api/keycloak/users : Récupère tous les utilisateurs
     *
     * @return Liste de tous les utilisateurs Keycloak
     */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        LOG.debug("REST request to get all Keycloak users");
        List<Map<String, Object>> users = keycloakUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/keycloak/users/email/{email} : Récupère un utilisateur par email
     *
     * @param email Email de l'utilisateur
     * @return L'utilisateur trouvé ou 404
     */
    @GetMapping("/users/email/{email}")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable String email) {
        LOG.debug("REST request to get Keycloak user by email: {}", email);
        Map<String, Object> user = keycloakUserService.getUserByEmail(email);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/keycloak/users/{userId} : Récupère un utilisateur par ID
     *
     * @param userId ID Keycloak de l'utilisateur
     * @return L'utilisateur trouvé
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String userId) {
        LOG.debug("REST request to get Keycloak user by ID: {}", userId);
        Map<String, Object> user = keycloakUserService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * GET /api/keycloak/users/{userId}/roles : Récupère les rôles d'un utilisateur
     *
     * @param userId ID Keycloak de l'utilisateur
     * @return Liste des rôles
     */
    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<List<Map<String, Object>>> getUserRoles(@PathVariable String userId) {
        LOG.debug("REST request to get roles for Keycloak user: {}", userId);
        List<Map<String, Object>> roles = keycloakUserService.getUserRoles(userId);
        return ResponseEntity.ok(roles);
    }

    /**
     * GET /api/keycloak/users/details/{email} : Récupère les détails complets (avec rôles)
     *
     * @param email Email de l'utilisateur
     * @return Détails complets de l'utilisateur
     */
    @GetMapping("/users/details/{email}")
    public ResponseEntity<Map<String, Object>> getUserFullDetails(@PathVariable String email) {
        LOG.debug("REST request to get full details for Keycloak user: {}", email);
        Map<String, Object> userDetails = keycloakUserService.getUserFullDetails(email);

        if (userDetails != null) {
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/keycloak/users/count : Compte le nombre d'utilisateurs
     *
     * @return Nombre total d'utilisateurs
     */
    @GetMapping("/users/count")
    public ResponseEntity<Map<String, Integer>> getUserCount() {
        LOG.debug("REST request to count Keycloak users");
        int count = keycloakUserService.getUserCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
}
