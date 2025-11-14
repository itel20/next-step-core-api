package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.config.ApplicationProperties;
import com.nextstepsenegal.app.domain.AuthentificationByToken;
import com.nextstepsenegal.app.domain.Eleve;
import com.nextstepsenegal.app.repository.EleveRepository;
import com.nextstepsenegal.app.repository.search.EleveSearchRepository;
import com.nextstepsenegal.app.service.dto.EleveDTO;
import com.nextstepsenegal.app.service.mapper.EleveMapper;
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
 * Service Implementation for managing {@link com.nextstepsenegal.app.domain.Eleve}.
 */
@Service
@Transactional
public class EleveService {

    private static final Logger LOG = LoggerFactory.getLogger(EleveService.class);

    private final EleveRepository eleveRepository;

    private final EleveMapper eleveMapper;

    private final EleveSearchRepository eleveSearchRepository;

    private final ApplicationProperties applicationProperties;

    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;

    private final AuthentificationByToken authentificationByToken;

    public EleveService(
        EleveRepository eleveRepository,
        EleveMapper eleveMapper,
        EleveSearchRepository eleveSearchRepository,
        AuthentificationByToken authentificationByToken,
        RestTemplate restTemplate,
        ApplicationProperties applicationProperties,
        PasswordEncoder passwordEncoder
    ) {
        this.eleveRepository = eleveRepository;
        this.eleveMapper = eleveMapper;
        this.eleveSearchRepository = eleveSearchRepository;
        this.authentificationByToken = authentificationByToken;
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Save an Eleve.
     *
     * @param eleveDTO the DTO to save.
     * @return the persisted entity.
     */
    @Transactional
    public EleveDTO save(EleveDTO eleveDTO) {
        LOG.debug("Request to save Eleve : {}", eleveDTO);

        // Vérification de la présence de l'email
        if (eleveDTO.getEmail() == null || eleveDTO.getEmail().trim().isEmpty()) {
            LOG.error("Tentative de création d'un élève sans email");
            throw new IllegalArgumentException("L'email est obligatoire pour créer un élève");
        }

        // Vérification si l'email existe déjà dans la base de données locale
        Optional<Eleve> existingEleve = eleveRepository.findByEmail(eleveDTO.getEmail());
        if (existingEleve.isPresent()) {
            LOG.warn("Un élève avec l'email {} existe déjà - ID: {}", eleveDTO.getEmail(), existingEleve.get().getId());
            throw new IllegalArgumentException("Un élève avec cet email existe déjà");
        }

        LOG.info("Email validé: {}", eleveDTO.getEmail());

        // Authentification Keycloak
        LOG.info(" Authentification Keycloak...");
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
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", eleveDTO.getEmail());
        userData.put("email", eleveDTO.getEmail());
        userData.put("enabled", true);
        userData.put("firstName", eleveDTO.getPrenom());
        userData.put("lastName", eleveDTO.getNom());
        userData.put("credentials", List.of(Map.of("type", "password", "value", eleveDTO.getPassword(), "temporary", false)));

        LOG.debug("Données utilisateur préparées: username={}, email={}", eleveDTO.getEmail(), eleveDTO.getEmail());

        // Création de l'utilisateur dans Keycloak
        String createUserUrl = applicationProperties.getKcUrl1();
        LOG.info("URL de création Keycloak: {}", createUserUrl);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userData, headers);
        String userId = null;

        try {
            LOG.info("Envoi de la requête de création pour: {}", eleveDTO.getEmail());

            ResponseEntity<Void> response = restTemplate.exchange(createUserUrl, HttpMethod.POST, request, Void.class);

            LOG.info("Réponse Keycloak - Status: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur Keycloak: " + response.getStatusCode());
            }

            if (response.getHeaders().getLocation() != null) {
                String location = response.getHeaders().getLocation().toString();
                userId = location.substring(location.lastIndexOf('/') + 1);
                LOG.info("Utilisateur créé avec ID: {}", userId);
            }
        } catch (HttpClientErrorException.Conflict e) {
            LOG.warn("Utilisateur existe déjà dans Keycloak: {}", eleveDTO.getEmail());
            LOG.debug("Détails conflit: {}", e.getResponseBodyAsString());
        } catch (HttpClientErrorException e) {
            LOG.error("Erreur HTTP Keycloak - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erreur création Keycloak: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOG.error("Erreur inattendue lors de la création Keycloak", e);
            throw new RuntimeException("Erreur Keycloak: " + e.getMessage(), e);
        }

        // Récupération de l'ID Keycloak
        if (userId == null) {
            LOG.info("Recherche de l'utilisateur dans Keycloak: {}", eleveDTO.getEmail());

            try {
                String searchUrl = createUserUrl + "?username=" + eleveDTO.getEmail();
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

        // Attribution du rôle "user"
        try {
            LOG.info("Attribution du rôle 'user' à: {}", eleveDTO.getEmail());

            String baseUrl = applicationProperties.getKcBaseUrl();
            String realm = applicationProperties.getKcRealm();

            String getRoleUrl = baseUrl + "/admin/realms/" + realm + "/roles/user";
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

            LOG.info("Rôle 'user' assigné à {}", eleveDTO.getEmail());
        } catch (Exception e) {
            LOG.warn("Rôle non assigné : {}", e.getMessage());
        }

        // Sauvegarde dans la base de données locale
        LOG.info("Sauvegarde en base de données locale...");

        Eleve eleve = eleveMapper.toEntity(eleveDTO);

        eleve.setPasswordHash(passwordEncoder.encode(eleveDTO.getPassword()));
        eleve.setPassword(null);
        eleve.setKeycloakId(userId);

        eleve = eleveRepository.save(eleve);

        LOG.info("Eleve sauvegardé avec succès - ID: {}, Keycloak ID: {}", eleve.getId(), userId);

        return eleveMapper.toDto(eleve);
    }

    /**
     * Update a eleve.
     *
     * @param eleveDTO the entity to save.
     * @return the persisted entity.
     */
    public EleveDTO update(EleveDTO eleveDTO) {
        LOG.debug("Request to update Eleve : {}", eleveDTO);
        Eleve eleve = eleveMapper.toEntity(eleveDTO);
        eleve = eleveRepository.save(eleve);
        eleveSearchRepository.index(eleve);
        return eleveMapper.toDto(eleve);
    }

    /**
     * Partially update a eleve.
     *
     * @param eleveDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EleveDTO> partialUpdate(EleveDTO eleveDTO) {
        LOG.debug("Request to partially update Eleve : {}", eleveDTO);

        return eleveRepository
            .findById(eleveDTO.getId())
            .map(existingEleve -> {
                eleveMapper.partialUpdate(existingEleve, eleveDTO);

                return existingEleve;
            })
            .map(eleveRepository::save)
            .map(savedEleve -> {
                eleveSearchRepository.index(savedEleve);
                return savedEleve;
            })
            .map(eleveMapper::toDto);
    }

    /**
     * Get all the eleves.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EleveDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Eleves");
        return eleveRepository.findAll(pageable).map(eleveMapper::toDto);
    }

    /**
     * Get all the eleves with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EleveDTO> findAllWithEagerRelationships(Pageable pageable) {
        return eleveRepository.findAllWithEagerRelationships(pageable).map(eleveMapper::toDto);
    }

    /**
     * Get one eleve by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EleveDTO> findOne(Long id) {
        LOG.debug("Request to get Eleve : {}", id);
        return eleveRepository.findOneWithEagerRelationships(id).map(eleveMapper::toDto);
    }

    /**
     * Delete the Eleve by id (Keycloak + Base de données locale).
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        LOG.debug(" Request to delete Eleve : {}", id);

        // 1 Récupérer l'élève dans la base
        Eleve eleve = eleveRepository.findById(id).orElseThrow(() -> new RuntimeException("Élève introuvable avec l'ID : " + id));

        // 2 Vérifier s’il a un identifiant Keycloak
        if (eleve.getKeycloakId() != null) {
            try {
                LOG.info(" Suppression de l'utilisateur Keycloak ID: {}", eleve.getKeycloakId());

                // Authentification Keycloak
                String accessToken = authentificationByToken.authentificationFonction(
                    applicationProperties.getKcUser(),
                    applicationProperties.getKcPassword()
                );

                if (accessToken == null || accessToken.isEmpty()) {
                    throw new RuntimeException("Échec d'authentification Keycloak - Token null ou vide");
                }

                // Préparation headers
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                // URL de suppression Keycloak
                String deleteUrl =
                    applicationProperties.getKcBaseUrl() +
                    "/admin/realms/" +
                    applicationProperties.getKcRealm() +
                    "/users/" +
                    eleve.getKeycloakId();

                LOG.debug("URL suppression Keycloak: {}", deleteUrl);

                restTemplate.exchange(deleteUrl, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

                LOG.info(" Utilisateur Keycloak supprimé avec succès: {}", eleve.getKeycloakId());
            } catch (HttpClientErrorException.NotFound e) {
                LOG.warn(" Utilisateur Keycloak non trouvé pour ID: {}", eleve.getKeycloakId());
            } catch (Exception e) {
                LOG.error(" Erreur lors de la suppression dans Keycloak", e);
                throw new RuntimeException("Erreur suppression Keycloak: " + e.getMessage(), e);
            }
        } else {
            LOG.warn(" Aucun ID Keycloak associé à cet élève, suppression uniquement en base.");
        }

        // 3️ Suppression dans la base de données locale
        LOG.info(" Suppression de l'élève dans la base locale (ID: {})", id);
        eleveRepository.deleteById(id);
        eleveSearchRepository.deleteFromIndexById(id);

        LOG.info(" Élève supprimé avec succès (base locale + Keycloak).");
    }

    /**
     * Search for the eleve corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EleveDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Eleves for query {}", query);
        return eleveSearchRepository.search(query, pageable).map(eleveMapper::toDto);
    }
}
