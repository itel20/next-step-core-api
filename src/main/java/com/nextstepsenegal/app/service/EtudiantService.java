package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.config.ApplicationProperties;
import com.nextstepsenegal.app.domain.AuthentificationByToken;
import com.nextstepsenegal.app.domain.Etudiant;
import com.nextstepsenegal.app.repository.EtudiantRepository;
import com.nextstepsenegal.app.repository.search.EtudiantSearchRepository;
import com.nextstepsenegal.app.service.dto.EtudiantDTO;
import com.nextstepsenegal.app.service.mapper.EtudiantMapper;
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
 * Service Implementation for managing {@link com.nextstepsenegal.app.domain.Etudiant}.
 */
@Service
@Transactional
public class EtudiantService {

    private static final Logger LOG = LoggerFactory.getLogger(EtudiantService.class);

    private final EtudiantRepository etudiantRepository;

    private final EtudiantMapper etudiantMapper;

    private final EtudiantSearchRepository etudiantSearchRepository;

    private final AuthentificationByToken authentificationByToken;

    private final ApplicationProperties applicationProperties;

    private final RestTemplate restTemplate;

    private final PasswordEncoder passwordEncoder;

    public EtudiantService(
        EtudiantRepository etudiantRepository,
        EtudiantMapper etudiantMapper,
        EtudiantSearchRepository etudiantSearchRepository,
        ApplicationProperties applicationProperties,
        AuthentificationByToken authentificationByToken,
        RestTemplate restTemplate,
        PasswordEncoder passwordEncoder
    ) {
        this.etudiantRepository = etudiantRepository;
        this.etudiantMapper = etudiantMapper;
        this.etudiantSearchRepository = etudiantSearchRepository;
        this.applicationProperties = applicationProperties;
        this.authentificationByToken = authentificationByToken;
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Save an Etudiant.
     *
     * @param etudiantDTO the DTO to save.
     * @return the persisted entity.
     */
    @Transactional
    public EtudiantDTO save(EtudiantDTO etudiantDTO) {
        LOG.debug("Request to save Etudiant : {}", etudiantDTO);

        // Vérification de la présence de l'email
        if (etudiantDTO.getEmail() == null || etudiantDTO.getEmail().trim().isEmpty()) {
            LOG.error("Tentative de création d'un etudiant sans email");
            throw new IllegalArgumentException("L'email est obligatoire pour créer un etudiant");
        }

        // Vérification si l'email existe déjà dans la base de données locale
        Optional<Etudiant> existingEleve = etudiantRepository.findByEmail(etudiantDTO.getEmail());
        if (existingEleve.isPresent()) {
            LOG.warn("Un élève avec l'email {} existe déjà - ID: {}", etudiantDTO.getEmail(), existingEleve.get().getId());
            throw new IllegalArgumentException("Un élève avec cet email existe déjà");
        }

        LOG.info("Email validé: {}", etudiantDTO.getEmail());

        //Authentification Keycloak
        LOG.info("🔐 Authentification Keycloak...");
        String accessToken = authentificationByToken.authentificationFonction(
            applicationProperties.getKcUser(),
            applicationProperties.getKcPassword()
        );

        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("Échec d'authentification Keycloak - Token null ou vide");
        }
        LOG.debug("Token récupéré: {}...", accessToken.substring(0, Math.min(20, accessToken.length())));

        //Préparation des headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        //Préparation des données utilisateur Keycloak
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", etudiantDTO.getEmail());
        userData.put("email", etudiantDTO.getEmail());
        userData.put("enabled", true);
        userData.put("firstName", etudiantDTO.getPrenom());
        userData.put("lastName", etudiantDTO.getNom());
        userData.put("credentials", List.of(Map.of("type", "password", "value", etudiantDTO.getPassword(), "temporary", false)));

        LOG.debug("Données utilisateur préparées: username={}, email={}", etudiantDTO.getEmail(), etudiantDTO.getEmail());

        //Création de l'utilisateur dans Keycloak
        String createUserUrl = applicationProperties.getKcUrl1();
        LOG.info("URL de création Keycloak: {}", createUserUrl);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userData, headers);
        String userId = null;

        try {
            LOG.info("Envoi de la requête de création pour: {}", etudiantDTO.getEmail());

            ResponseEntity<Void> response = restTemplate.exchange(createUserUrl, HttpMethod.POST, request, Void.class);

            LOG.info("Réponse Keycloak - Status: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erreur Keycloak: " + response.getStatusCode());
            }

            // Récupération de l'URL de location (contient l'ID)
            if (response.getHeaders().getLocation() != null) {
                String location = response.getHeaders().getLocation().toString();
                userId = location.substring(location.lastIndexOf('/') + 1);
                LOG.info("Utilisateur créé avec ID: {}", userId);
            }
        } catch (HttpClientErrorException.Conflict e) {
            LOG.warn("Utilisateur existe déjà dans Keycloak: {}", etudiantDTO.getEmail());
            LOG.debug("Détails conflit: {}", e.getResponseBodyAsString());
            // On continue pour récupérer l'ID existant

        } catch (HttpClientErrorException e) {
            LOG.error("Erreur HTTP Keycloak - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erreur création Keycloak: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            LOG.error("Erreur inattendue lors de la création Keycloak", e);
            throw new RuntimeException("Erreur Keycloak: " + e.getMessage(), e);
        }

        // Récupération de l'ID Keycloak (si pas récupéré via Location)
        if (userId == null) {
            LOG.info("Recherche de l'utilisateur dans Keycloak: {}", etudiantDTO.getEmail());

            try {
                String searchUrl = createUserUrl + "?username=" + etudiantDTO.getEmail();
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
            LOG.info("👤 Attribution du rôle 'user' à: {}", etudiantDTO.getEmail());

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

            LOG.info("Rôle 'user' assigné à {}", etudiantDTO.getEmail());
        } catch (Exception e) {
            LOG.warn("Rôle non assigné : {}", e.getMessage());
        }

        // Sauvegarde dans la base de données locale
        LOG.info("Sauvegarde en base de données locale...");

        Etudiant etudiant = etudiantMapper.toEntity(etudiantDTO);

        //on NE stocke PAS le mot de passe clair !
        etudiant.setPasswordHash(passwordEncoder.encode(etudiantDTO.getPassword()));
        etudiant.setPassword(null); // Nettoyage du mot de passe clair
        etudiant.setKeycloakId(userId); // Lier l'utilisateur Keycloak

        etudiant = etudiantRepository.save(etudiant);

        LOG.info("Etudiant sauvegardé avec succès - ID: {}, Keycloak ID: {}", etudiant.getId(), userId);

        return etudiantMapper.toDto(etudiant);
    }

    /**
     * Update a etudiant.
     *
     * @param etudiantDTO the entity to save.
     * @return the persisted entity.
     */
    public EtudiantDTO update(EtudiantDTO etudiantDTO) {
        LOG.debug("Request to update Etudiant : {}", etudiantDTO);
        Etudiant etudiant = etudiantMapper.toEntity(etudiantDTO);
        etudiant = etudiantRepository.save(etudiant);
        etudiantSearchRepository.index(etudiant);
        return etudiantMapper.toDto(etudiant);
    }

    /**
     * Partially update a etudiant.
     *
     * @param etudiantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EtudiantDTO> partialUpdate(EtudiantDTO etudiantDTO) {
        LOG.debug("Request to partially update Etudiant : {}", etudiantDTO);

        return etudiantRepository
            .findById(etudiantDTO.getId())
            .map(existingEtudiant -> {
                etudiantMapper.partialUpdate(existingEtudiant, etudiantDTO);

                return existingEtudiant;
            })
            .map(etudiantRepository::save)
            .map(savedEtudiant -> {
                etudiantSearchRepository.index(savedEtudiant);
                return savedEtudiant;
            })
            .map(etudiantMapper::toDto);
    }

    /**
     * Get all the etudiants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EtudiantDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Etudiants");
        return etudiantRepository.findAll(pageable).map(etudiantMapper::toDto);
    }

    /**
     * Get all the etudiants with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EtudiantDTO> findAllWithEagerRelationships(Pageable pageable) {
        return etudiantRepository.findAllWithEagerRelationships(pageable).map(etudiantMapper::toDto);
    }

    /**
     * Get one etudiant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EtudiantDTO> findOne(Long id) {
        LOG.debug("Request to get Etudiant : {}", id);
        return etudiantRepository.findOneWithEagerRelationships(id).map(etudiantMapper::toDto);
    }

    /**
     * Delete the etudiant by id (Keycloak + base de données locale).
     *
     * @param id the id of the entity.
     */
    @Transactional
    public void delete(Long id) {
        LOG.debug(" Request to delete Etudiant : {}", id);

        // 1 Récupérer l'étudiant dans la base
        Etudiant etudiant = etudiantRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Étudiant introuvable avec l'ID : " + id));

        // 2 Vérifier s’il a un identifiant Keycloak
        if (etudiant.getKeycloakId() != null) {
            try {
                LOG.info(" Suppression de l'utilisateur Keycloak ID: {}", etudiant.getKeycloakId());

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
                    etudiant.getKeycloakId();

                LOG.debug("URL suppression Keycloak: {}", deleteUrl);

                restTemplate.exchange(deleteUrl, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

                LOG.info(" Utilisateur Keycloak supprimé avec succès: {}", etudiant.getKeycloakId());
            } catch (HttpClientErrorException.NotFound e) {
                LOG.warn(" Utilisateur Keycloak non trouvé pour ID: {}", etudiant.getKeycloakId());
            } catch (Exception e) {
                LOG.error(" Erreur lors de la suppression dans Keycloak", e);
                throw new RuntimeException("Erreur suppression Keycloak: " + e.getMessage(), e);
            }
        } else {
            LOG.warn(" Aucun ID Keycloak associé à cet étudiant, suppression uniquement en base.");
        }

        // 3 Suppression dans la base de données locale
        LOG.info(" Suppression de l'étudiant dans la base locale (ID: {})", id);
        etudiantRepository.deleteById(id);
        etudiantSearchRepository.deleteFromIndexById(id);

        LOG.info(" Étudiant supprimé avec succès (base locale + Keycloak).");
    }

    /**
     * Search for the etudiant corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EtudiantDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Etudiants for query {}", query);
        return etudiantSearchRepository.search(query, pageable).map(etudiantMapper::toDto);
    }
}
