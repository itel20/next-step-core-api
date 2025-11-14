package com.nextstepsenegal.app.web.rest;

import static com.nextstepsenegal.app.domain.EleveAsserts.*;
import static com.nextstepsenegal.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextstepsenegal.app.IntegrationTest;
import com.nextstepsenegal.app.domain.Eleve;
import com.nextstepsenegal.app.repository.EleveRepository;
import com.nextstepsenegal.app.repository.UserRepository;
import com.nextstepsenegal.app.repository.search.EleveSearchRepository;
import com.nextstepsenegal.app.service.EleveService;
import com.nextstepsenegal.app.service.dto.EleveDTO;
import com.nextstepsenegal.app.service.mapper.EleveMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EleveResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EleveResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_NAISSANCE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_NAISSANCE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_SERIE = "AAAAAAAAAA";
    private static final String UPDATED_SERIE = "BBBBBBBBBB";

    private static final String DEFAULT_NIVEAU_ETUDE = "AAAAAAAAAA";
    private static final String UPDATED_NIVEAU_ETUDE = "BBBBBBBBBB";

    private static final String DEFAULT_LYCEE = "AAAAAAAAAA";
    private static final String UPDATED_LYCEE = "BBBBBBBBBB";

    private static final String DEFAULT_VILLE = "AAAAAAAAAA";
    private static final String UPDATED_VILLE = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD_HASH = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_KEYCLOAK_ID = "AAAAAAAAAA";
    private static final String UPDATED_KEYCLOAK_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/eleves";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/eleves/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EleveRepository eleveRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private EleveRepository eleveRepositoryMock;

    @Autowired
    private EleveMapper eleveMapper;

    @Mock
    private EleveService eleveServiceMock;

    @Autowired
    private EleveSearchRepository eleveSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEleveMockMvc;

    private Eleve eleve;

    private Eleve insertedEleve;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Eleve createEntity() {
        return new Eleve()
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .dateNaissance(DEFAULT_DATE_NAISSANCE)
            .telephone(DEFAULT_TELEPHONE)
            .adresse(DEFAULT_ADRESSE)
            .email(DEFAULT_EMAIL)
            .serie(DEFAULT_SERIE)
            .niveauEtude(DEFAULT_NIVEAU_ETUDE)
            .lycee(DEFAULT_LYCEE)
            .ville(DEFAULT_VILLE)
            .password(DEFAULT_PASSWORD)
            .passwordHash(DEFAULT_PASSWORD_HASH)
            .keycloakId(DEFAULT_KEYCLOAK_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Eleve createUpdatedEntity() {
        return new Eleve()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .telephone(UPDATED_TELEPHONE)
            .adresse(UPDATED_ADRESSE)
            .email(UPDATED_EMAIL)
            .serie(UPDATED_SERIE)
            .niveauEtude(UPDATED_NIVEAU_ETUDE)
            .lycee(UPDATED_LYCEE)
            .ville(UPDATED_VILLE)
            .password(UPDATED_PASSWORD)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .keycloakId(UPDATED_KEYCLOAK_ID);
    }

    @BeforeEach
    public void initTest() {
        eleve = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEleve != null) {
            eleveRepository.delete(insertedEleve);
            eleveSearchRepository.delete(insertedEleve);
            insertedEleve = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createEleve() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        // Create the Eleve
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);
        var returnedEleveDTO = om.readValue(
            restEleveMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eleveDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EleveDTO.class
        );

        // Validate the Eleve in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEleve = eleveMapper.toEntity(returnedEleveDTO);
        assertEleveUpdatableFieldsEquals(returnedEleve, getPersistedEleve(returnedEleve));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedEleve = returnedEleve;
    }

    @Test
    @Transactional
    void createEleveWithExistingId() throws Exception {
        // Create the Eleve with an existing ID
        eleve.setId(1L);
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEleveMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eleveDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Eleve in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        // set the field null
        eleve.setNom(null);

        // Create the Eleve, which fails.
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        restEleveMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eleveDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPrenomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        // set the field null
        eleve.setPrenom(null);

        // Create the Eleve, which fails.
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        restEleveMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eleveDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        // set the field null
        eleve.setEmail(null);

        // Create the Eleve, which fails.
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        restEleveMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eleveDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSerieIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        // set the field null
        eleve.setSerie(null);

        // Create the Eleve, which fails.
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        restEleveMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eleveDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNiveauEtudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        // set the field null
        eleve.setNiveauEtude(null);

        // Create the Eleve, which fails.
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        restEleveMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eleveDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPasswordHashIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        // set the field null
        eleve.setPasswordHash(null);

        // Create the Eleve, which fails.
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        restEleveMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eleveDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEleves() throws Exception {
        // Initialize the database
        insertedEleve = eleveRepository.saveAndFlush(eleve);

        // Get all the eleveList
        restEleveMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eleve.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].serie").value(hasItem(DEFAULT_SERIE)))
            .andExpect(jsonPath("$.[*].niveauEtude").value(hasItem(DEFAULT_NIVEAU_ETUDE)))
            .andExpect(jsonPath("$.[*].lycee").value(hasItem(DEFAULT_LYCEE)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].passwordHash").value(hasItem(DEFAULT_PASSWORD_HASH)))
            .andExpect(jsonPath("$.[*].keycloakId").value(hasItem(DEFAULT_KEYCLOAK_ID)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllElevesWithEagerRelationshipsIsEnabled() throws Exception {
        when(eleveServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEleveMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(eleveServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllElevesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(eleveServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEleveMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(eleveRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEleve() throws Exception {
        // Initialize the database
        insertedEleve = eleveRepository.saveAndFlush(eleve);

        // Get the eleve
        restEleveMockMvc
            .perform(get(ENTITY_API_URL_ID, eleve.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(eleve.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.dateNaissance").value(DEFAULT_DATE_NAISSANCE.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.serie").value(DEFAULT_SERIE))
            .andExpect(jsonPath("$.niveauEtude").value(DEFAULT_NIVEAU_ETUDE))
            .andExpect(jsonPath("$.lycee").value(DEFAULT_LYCEE))
            .andExpect(jsonPath("$.ville").value(DEFAULT_VILLE))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.passwordHash").value(DEFAULT_PASSWORD_HASH))
            .andExpect(jsonPath("$.keycloakId").value(DEFAULT_KEYCLOAK_ID));
    }

    @Test
    @Transactional
    void getNonExistingEleve() throws Exception {
        // Get the eleve
        restEleveMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEleve() throws Exception {
        // Initialize the database
        insertedEleve = eleveRepository.saveAndFlush(eleve);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        eleveSearchRepository.save(eleve);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());

        // Update the eleve
        Eleve updatedEleve = eleveRepository.findById(eleve.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEleve are not directly saved in db
        em.detach(updatedEleve);
        updatedEleve
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .telephone(UPDATED_TELEPHONE)
            .adresse(UPDATED_ADRESSE)
            .email(UPDATED_EMAIL)
            .serie(UPDATED_SERIE)
            .niveauEtude(UPDATED_NIVEAU_ETUDE)
            .lycee(UPDATED_LYCEE)
            .ville(UPDATED_VILLE)
            .password(UPDATED_PASSWORD)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .keycloakId(UPDATED_KEYCLOAK_ID);
        EleveDTO eleveDTO = eleveMapper.toDto(updatedEleve);

        restEleveMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eleveDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eleveDTO))
            )
            .andExpect(status().isOk());

        // Validate the Eleve in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEleveToMatchAllProperties(updatedEleve);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Eleve> eleveSearchList = Streamable.of(eleveSearchRepository.findAll()).toList();
                Eleve testEleveSearch = eleveSearchList.get(searchDatabaseSizeAfter - 1);

                assertEleveAllPropertiesEquals(testEleveSearch, updatedEleve);
            });
    }

    @Test
    @Transactional
    void putNonExistingEleve() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        eleve.setId(longCount.incrementAndGet());

        // Create the Eleve
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eleveDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eleveDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eleve in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEleve() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        eleve.setId(longCount.incrementAndGet());

        // Create the Eleve
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eleveDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eleve in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEleve() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        eleve.setId(longCount.incrementAndGet());

        // Create the Eleve
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eleveDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Eleve in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEleveWithPatch() throws Exception {
        // Initialize the database
        insertedEleve = eleveRepository.saveAndFlush(eleve);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eleve using partial update
        Eleve partialUpdatedEleve = new Eleve();
        partialUpdatedEleve.setId(eleve.getId());

        partialUpdatedEleve
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .telephone(UPDATED_TELEPHONE)
            .adresse(UPDATED_ADRESSE)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .keycloakId(UPDATED_KEYCLOAK_ID);

        restEleveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEleve.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEleve))
            )
            .andExpect(status().isOk());

        // Validate the Eleve in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEleveUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEleve, eleve), getPersistedEleve(eleve));
    }

    @Test
    @Transactional
    void fullUpdateEleveWithPatch() throws Exception {
        // Initialize the database
        insertedEleve = eleveRepository.saveAndFlush(eleve);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the eleve using partial update
        Eleve partialUpdatedEleve = new Eleve();
        partialUpdatedEleve.setId(eleve.getId());

        partialUpdatedEleve
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .telephone(UPDATED_TELEPHONE)
            .adresse(UPDATED_ADRESSE)
            .email(UPDATED_EMAIL)
            .serie(UPDATED_SERIE)
            .niveauEtude(UPDATED_NIVEAU_ETUDE)
            .lycee(UPDATED_LYCEE)
            .ville(UPDATED_VILLE)
            .password(UPDATED_PASSWORD)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .keycloakId(UPDATED_KEYCLOAK_ID);

        restEleveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEleve.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEleve))
            )
            .andExpect(status().isOk());

        // Validate the Eleve in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEleveUpdatableFieldsEquals(partialUpdatedEleve, getPersistedEleve(partialUpdatedEleve));
    }

    @Test
    @Transactional
    void patchNonExistingEleve() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        eleve.setId(longCount.incrementAndGet());

        // Create the Eleve
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eleveDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eleveDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eleve in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEleve() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        eleve.setId(longCount.incrementAndGet());

        // Create the Eleve
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eleveDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Eleve in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEleve() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        eleve.setId(longCount.incrementAndGet());

        // Create the Eleve
        EleveDTO eleveDTO = eleveMapper.toDto(eleve);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEleveMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eleveDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Eleve in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEleve() throws Exception {
        // Initialize the database
        insertedEleve = eleveRepository.saveAndFlush(eleve);
        eleveRepository.save(eleve);
        eleveSearchRepository.save(eleve);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the eleve
        restEleveMockMvc
            .perform(delete(ENTITY_API_URL_ID, eleve.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(eleveSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEleve() throws Exception {
        // Initialize the database
        insertedEleve = eleveRepository.saveAndFlush(eleve);
        eleveSearchRepository.save(eleve);

        // Search the eleve
        restEleveMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + eleve.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(eleve.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].serie").value(hasItem(DEFAULT_SERIE)))
            .andExpect(jsonPath("$.[*].niveauEtude").value(hasItem(DEFAULT_NIVEAU_ETUDE)))
            .andExpect(jsonPath("$.[*].lycee").value(hasItem(DEFAULT_LYCEE)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].passwordHash").value(hasItem(DEFAULT_PASSWORD_HASH)))
            .andExpect(jsonPath("$.[*].keycloakId").value(hasItem(DEFAULT_KEYCLOAK_ID)));
    }

    protected long getRepositoryCount() {
        return eleveRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Eleve getPersistedEleve(Eleve eleve) {
        return eleveRepository.findById(eleve.getId()).orElseThrow();
    }

    protected void assertPersistedEleveToMatchAllProperties(Eleve expectedEleve) {
        assertEleveAllPropertiesEquals(expectedEleve, getPersistedEleve(expectedEleve));
    }

    protected void assertPersistedEleveToMatchUpdatableProperties(Eleve expectedEleve) {
        assertEleveAllUpdatablePropertiesEquals(expectedEleve, getPersistedEleve(expectedEleve));
    }
}
