package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.config.ApplicationProperties;
import com.nextstepsenegal.app.domain.AuthentificationByToken;
import com.nextstepsenegal.app.domain.Conseiller;
import com.nextstepsenegal.app.repository.ConseillerRepository;
import com.nextstepsenegal.app.repository.search.ConseillerSearchRepository;
import com.nextstepsenegal.app.service.dto.ConseillerDTO;
import com.nextstepsenegal.app.service.mapper.ConseillerMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Service Implementation for managing {@link com.nextstepsenegal.app.domain.Conseiller}.
 */
@Service
@Transactional
public class ConseillerService {

    private static final Logger LOG = LoggerFactory.getLogger(ConseillerService.class);

    private final ConseillerRepository conseillerRepository;

    private final ConseillerMapper conseillerMapper;

    private final ConseillerSearchRepository conseillerSearchRepository;

    private final ApplicationProperties applicationProperties;

    private final AuthentificationByToken authentificationByToken;

    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;

    public ConseillerService(
        ConseillerRepository conseillerRepository,
        ConseillerMapper conseillerMapper,
        ConseillerSearchRepository conseillerSearchRepository,
        AuthentificationByToken authentificationByToken,
        ApplicationProperties applicationProperties,
        PasswordEncoder passwordEncoder,
        RestTemplate restTemplate
    ) {
        this.conseillerRepository = conseillerRepository;
        this.conseillerMapper = conseillerMapper;
        this.conseillerSearchRepository = conseillerSearchRepository;
        this.authentificationByToken = authentificationByToken;
        this.applicationProperties = applicationProperties;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
    }

    /**
     * Save a Conseiller.
     *
     * @param conseillerDTO the DTO to save.
     * @return the persisted entity.
     */
    @Transactional
    public ConseillerDTO save(ConseillerDTO conseillerDTO) {
        LOG.debug("Request to save Conseiller : {}", conseillerDTO);

        // Vérification de la présence de l'email
        if (conseillerDTO.getEmail() == null || conseillerDTO.getEmail().trim().isEmpty()) {
            LOG.error("Tentative de création d'un conseiller sans email");
            throw new IllegalArgumentException("L'email est obligatoire pour créer un conseiller");
        }

        // Vérification si l'email existe déjà dans la base de données locale
        Optional<Conseiller> existingEleve = conseillerRepository.findByEmail(conseillerDTO.getEmail());
        if (existingEleve.isPresent()) {
            LOG.warn("Un élève avec l'email {} existe déjà - ID: {}", conseillerDTO.getEmail(), existingEleve.get().getId());
            throw new IllegalArgumentException("Un conseiller avec cet email existe déjà");
        }

        LOG.info("Email validé: {}", conseillerDTO.getEmail());

        // Authentification Keycloak
        LOG.info("Authentification Keycloak...");
        String accessToken = authentificationByToken.authentificationFonction(
            applicationProperties.getKcUser(),
            applicationProperties.getKcPassword()
        );

        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Échec d'authentification Keycloak - Token null ou vide");
        }
        LOG.debug("Token récupéré: {}...", accessToken.substring(0, Math.min(20, accessToken.length())));

        // Préparation des headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // Préparation des données utilisateur Keycloak
        String username = (conseillerDTO.getPrenom() + "." + conseillerDTO.getNom()).toLowerCase();

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", username + "@nextstepsenegal.com");
        userData.put("enabled", true);
        userData.put("firstName", conseillerDTO.getPrenom());
        userData.put("lastName", conseillerDTO.getNom());
        userData.put("credentials", List.of(Map.of("type", "password", "value", conseillerDTO.getPassword(), "temporary", false)));

        LOG.debug("Données utilisateur préparées: username={}, email={}", username, username + "@nextstepsenegal.com");

        // Création de l'utilisateur dans Keycloak
        String createUserUrl = applicationProperties.getKcUrl1();
        LOG.info("URL de création Keycloak: {}", createUserUrl);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userData, headers);
        String userId = null;

        try {
            LOG.info("Envoi de la requête de création pour: {}", username);

            ResponseEntity<Void> response = restTemplate.exchange(createUserUrl, HttpMethod.POST, request, Void.class);

            LOG.info("Réponse Keycloak - Status: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur Keycloak: " + response.getStatusCode());
            }

            // Récupération de l'URL de location (contenant l'ID)
            if (response.getHeaders().getLocation() != null) {
                String location = response.getHeaders().getLocation().toString();
                userId = location.substring(location.lastIndexOf('/') + 1);
                LOG.info("Utilisateur créé avec ID: {}", userId);
            }
        } catch (HttpClientErrorException.Conflict e) {
            LOG.warn("Utilisateur existe déjà dans Keycloak: {}", username);
            LOG.debug("Détails conflit: {}", e.getResponseBodyAsString());
            // On continue pour récupérer l'ID existant

        } catch (HttpClientErrorException e) {
            LOG.error("Erreur HTTP Keycloak - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erreur création Keycloak: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOG.error("Erreur inattendue lors de la création Keycloak", e);
            throw new RuntimeException("Erreur Keycloak: " + e.getMessage(), e);
        }

        // Récupération de l'ID Keycloak si non trouvé
        if (userId == null) {
            LOG.info("Recherche de l'utilisateur dans Keycloak: {}", username);

            try {
                String searchUrl = createUserUrl + "?username=" + username;
                LOG.debug("URL de recherche: {}", searchUrl);

                ResponseEntity<List<Map<String, Object>>> searchResponse = restTemplate.exchange(
                    searchUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
                );

                if (searchResponse.getBody() != null && !searchResponse.getBody().isEmpty()) {
                    userId = (String) searchResponse.getBody().get(0).get("id");
                    LOG.info("ID Keycloak récupéré: {}", userId);
                } else {
                    LOG.error("Utilisateur non trouvé dans Keycloak après création");
                    throw new RuntimeException("Utilisateur non trouvé dans Keycloak après création");
                }
            } catch (Exception e) {
                LOG.error("Erreur récupération ID Keycloak", e);
                throw new RuntimeException("Erreur récupération ID utilisateur: " + e.getMessage(), e);
            }
        }

        // Attribution du rôle "conseiller"
        try {
            LOG.info("Attribution du rôle 'conseiller' à: {}", username);

            String baseUrl = applicationProperties.getKcBaseUrl();
            String realm = applicationProperties.getKcRealm();

            String getRoleUrl = baseUrl + "/admin/realms/" + realm + "/roles/conseiller";
            LOG.debug("URL récupération rôle: {}", getRoleUrl);

            ResponseEntity<Map<String, Object>> roleResponse = restTemplate.exchange(
                getRoleUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> userRole = roleResponse.getBody();
            String roleUrl = baseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
            LOG.debug("URL attribution rôle: {}", roleUrl);

            restTemplate.exchange(roleUrl, HttpMethod.POST, new HttpEntity<>(List.of(userRole), headers), Void.class);

            LOG.info("Rôle 'conseiller' assigné à {}", username);
        } catch (Exception e) {
            LOG.warn("Rôle non assigné : {}", e.getMessage());
        }

        // Sauvegarde en base de données locale
        LOG.info("Sauvegarde du Conseiller en base de données locale...");

        Conseiller conseiller = conseillerMapper.toEntity(conseillerDTO);
        conseiller.setPasswordHash(passwordEncoder.encode(conseillerDTO.getPassword()));
        conseiller.setPassword(null); // on ne garde jamais le mot de passe clair
        conseiller.setKeycloakId(userId); // liaison avec Keycloak

        conseiller = conseillerRepository.save(conseiller);

        LOG.info("Conseiller sauvegardé avec succès - ID: {}, Keycloak ID: {}", conseiller.getId(), userId);

        return conseillerMapper.toDto(conseiller);
    }

    /**
     * Update a conseiller.
     *
     * @param conseillerDTO the entity to save.
     * @return the persisted entity.
     */
    public ConseillerDTO update(ConseillerDTO conseillerDTO) {
        LOG.debug("Request to update Conseiller : {}", conseillerDTO);
        Conseiller conseiller = conseillerMapper.toEntity(conseillerDTO);
        conseiller = conseillerRepository.save(conseiller);
        conseillerSearchRepository.index(conseiller);
        return conseillerMapper.toDto(conseiller);
    }

    /**
     * Partially update a conseiller.
     *
     * @param conseillerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ConseillerDTO> partialUpdate(ConseillerDTO conseillerDTO) {
        LOG.debug("Request to partially update Conseiller : {}", conseillerDTO);

        return conseillerRepository
            .findById(conseillerDTO.getId())
            .map(existingConseiller -> {
                conseillerMapper.partialUpdate(existingConseiller, conseillerDTO);

                return existingConseiller;
            })
            .map(conseillerRepository::save)
            .map(savedConseiller -> {
                conseillerSearchRepository.index(savedConseiller);
                return savedConseiller;
            })
            .map(conseillerMapper::toDto);
    }

    /**
     * Get all the conseillers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ConseillerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Conseillers");
        return conseillerRepository.findAll(pageable).map(conseillerMapper::toDto);
    }

    /**
     * Get all the conseillers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ConseillerDTO> findAllWithEagerRelationships(Pageable pageable) {
        return conseillerRepository.findAllWithEagerRelationships(pageable).map(conseillerMapper::toDto);
    }

    /**
     * Get one conseiller by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ConseillerDTO> findOne(Long id) {
        LOG.debug("Request to get Conseiller : {}", id);
        return conseillerRepository.findOneWithEagerRelationships(id).map(conseillerMapper::toDto);
    }

    /**
     * Delete the conseiller by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Conseiller : {}", id);
        conseillerRepository.deleteById(id);
        conseillerSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the conseiller corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ConseillerDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Conseillers for query {}", query);
        return conseillerSearchRepository.search(query, pageable).map(conseillerMapper::toDto);
    }
}
