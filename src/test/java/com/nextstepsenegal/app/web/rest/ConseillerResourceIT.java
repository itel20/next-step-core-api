package com.nextstepsenegal.app.web.rest;

import static com.nextstepsenegal.app.domain.ConseillerAsserts.*;
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
import com.nextstepsenegal.app.domain.Conseiller;
import com.nextstepsenegal.app.repository.ConseillerRepository;
import com.nextstepsenegal.app.repository.UserRepository;
import com.nextstepsenegal.app.repository.search.ConseillerSearchRepository;
import com.nextstepsenegal.app.service.ConseillerService;
import com.nextstepsenegal.app.service.dto.ConseillerDTO;
import com.nextstepsenegal.app.service.mapper.ConseillerMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ConseillerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ConseillerResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_SPECIALITE = "AAAAAAAAAA";
    private static final String UPDATED_SPECIALITE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_CERTIFIE = false;
    private static final Boolean UPDATED_CERTIFIE = true;

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD_HASH = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD_HASH = "BBBBBBBBBB";

    private static final String DEFAULT_KEYCLOAK_ID = "AAAAAAAAAA";
    private static final String UPDATED_KEYCLOAK_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/conseillers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/conseillers/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ConseillerRepository conseillerRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ConseillerRepository conseillerRepositoryMock;

    @Autowired
    private ConseillerMapper conseillerMapper;

    @Mock
    private ConseillerService conseillerServiceMock;

    @Autowired
    private ConseillerSearchRepository conseillerSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConseillerMockMvc;

    private Conseiller conseiller;

    private Conseiller insertedConseiller;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conseiller createEntity() {
        return new Conseiller()
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .specialite(DEFAULT_SPECIALITE)
            .email(DEFAULT_EMAIL)
            .description(DEFAULT_DESCRIPTION)
            .certifie(DEFAULT_CERTIFIE)
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
    public static Conseiller createUpdatedEntity() {
        return new Conseiller()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .specialite(UPDATED_SPECIALITE)
            .email(UPDATED_EMAIL)
            .description(UPDATED_DESCRIPTION)
            .certifie(UPDATED_CERTIFIE)
            .password(UPDATED_PASSWORD)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .keycloakId(UPDATED_KEYCLOAK_ID);
    }

    @BeforeEach
    public void initTest() {
        conseiller = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedConseiller != null) {
            conseillerRepository.delete(insertedConseiller);
            conseillerSearchRepository.delete(insertedConseiller);
            insertedConseiller = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createConseiller() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        // Create the Conseiller
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);
        var returnedConseillerDTO = om.readValue(
            restConseillerMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conseillerDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ConseillerDTO.class
        );

        // Validate the Conseiller in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedConseiller = conseillerMapper.toEntity(returnedConseillerDTO);
        assertConseillerUpdatableFieldsEquals(returnedConseiller, getPersistedConseiller(returnedConseiller));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedConseiller = returnedConseiller;
    }

    @Test
    @Transactional
    void createConseillerWithExistingId() throws Exception {
        // Create the Conseiller with an existing ID
        conseiller.setId(1L);
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restConseillerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conseillerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Conseiller in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        // set the field null
        conseiller.setNom(null);

        // Create the Conseiller, which fails.
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        restConseillerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conseillerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPrenomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        // set the field null
        conseiller.setPrenom(null);

        // Create the Conseiller, which fails.
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        restConseillerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conseillerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSpecialiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        // set the field null
        conseiller.setSpecialite(null);

        // Create the Conseiller, which fails.
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        restConseillerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conseillerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        // set the field null
        conseiller.setEmail(null);

        // Create the Conseiller, which fails.
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        restConseillerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conseillerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPasswordHashIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        // set the field null
        conseiller.setPasswordHash(null);

        // Create the Conseiller, which fails.
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        restConseillerMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conseillerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllConseillers() throws Exception {
        // Initialize the database
        insertedConseiller = conseillerRepository.saveAndFlush(conseiller);

        // Get all the conseillerList
        restConseillerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conseiller.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].specialite").value(hasItem(DEFAULT_SPECIALITE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].certifie").value(hasItem(DEFAULT_CERTIFIE.booleanValue())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].passwordHash").value(hasItem(DEFAULT_PASSWORD_HASH)))
            .andExpect(jsonPath("$.[*].keycloakId").value(hasItem(DEFAULT_KEYCLOAK_ID)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllConseillersWithEagerRelationshipsIsEnabled() throws Exception {
        when(conseillerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restConseillerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(conseillerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllConseillersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(conseillerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restConseillerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(conseillerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getConseiller() throws Exception {
        // Initialize the database
        insertedConseiller = conseillerRepository.saveAndFlush(conseiller);

        // Get the conseiller
        restConseillerMockMvc
            .perform(get(ENTITY_API_URL_ID, conseiller.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(conseiller.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.specialite").value(DEFAULT_SPECIALITE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.certifie").value(DEFAULT_CERTIFIE.booleanValue()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.passwordHash").value(DEFAULT_PASSWORD_HASH))
            .andExpect(jsonPath("$.keycloakId").value(DEFAULT_KEYCLOAK_ID));
    }

    @Test
    @Transactional
    void getNonExistingConseiller() throws Exception {
        // Get the conseiller
        restConseillerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingConseiller() throws Exception {
        // Initialize the database
        insertedConseiller = conseillerRepository.saveAndFlush(conseiller);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        conseillerSearchRepository.save(conseiller);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());

        // Update the conseiller
        Conseiller updatedConseiller = conseillerRepository.findById(conseiller.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedConseiller are not directly saved in db
        em.detach(updatedConseiller);
        updatedConseiller
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .specialite(UPDATED_SPECIALITE)
            .email(UPDATED_EMAIL)
            .description(UPDATED_DESCRIPTION)
            .certifie(UPDATED_CERTIFIE)
            .password(UPDATED_PASSWORD)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .keycloakId(UPDATED_KEYCLOAK_ID);
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(updatedConseiller);

        restConseillerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, conseillerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(conseillerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Conseiller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedConseillerToMatchAllProperties(updatedConseiller);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Conseiller> conseillerSearchList = Streamable.of(conseillerSearchRepository.findAll()).toList();
                Conseiller testConseillerSearch = conseillerSearchList.get(searchDatabaseSizeAfter - 1);

                assertConseillerAllPropertiesEquals(testConseillerSearch, updatedConseiller);
            });
    }

    @Test
    @Transactional
    void putNonExistingConseiller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        conseiller.setId(longCount.incrementAndGet());

        // Create the Conseiller
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConseillerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, conseillerDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(conseillerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conseiller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchConseiller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        conseiller.setId(longCount.incrementAndGet());

        // Create the Conseiller
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConseillerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(conseillerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conseiller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConseiller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        conseiller.setId(longCount.incrementAndGet());

        // Create the Conseiller
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConseillerMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(conseillerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Conseiller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateConseillerWithPatch() throws Exception {
        // Initialize the database
        insertedConseiller = conseillerRepository.saveAndFlush(conseiller);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conseiller using partial update
        Conseiller partialUpdatedConseiller = new Conseiller();
        partialUpdatedConseiller.setId(conseiller.getId());

        partialUpdatedConseiller
            .prenom(UPDATED_PRENOM)
            .specialite(UPDATED_SPECIALITE)
            .email(UPDATED_EMAIL)
            .description(UPDATED_DESCRIPTION)
            .certifie(UPDATED_CERTIFIE)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .keycloakId(UPDATED_KEYCLOAK_ID);

        restConseillerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConseiller.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConseiller))
            )
            .andExpect(status().isOk());

        // Validate the Conseiller in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConseillerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedConseiller, conseiller),
            getPersistedConseiller(conseiller)
        );
    }

    @Test
    @Transactional
    void fullUpdateConseillerWithPatch() throws Exception {
        // Initialize the database
        insertedConseiller = conseillerRepository.saveAndFlush(conseiller);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the conseiller using partial update
        Conseiller partialUpdatedConseiller = new Conseiller();
        partialUpdatedConseiller.setId(conseiller.getId());

        partialUpdatedConseiller
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .specialite(UPDATED_SPECIALITE)
            .email(UPDATED_EMAIL)
            .description(UPDATED_DESCRIPTION)
            .certifie(UPDATED_CERTIFIE)
            .password(UPDATED_PASSWORD)
            .passwordHash(UPDATED_PASSWORD_HASH)
            .keycloakId(UPDATED_KEYCLOAK_ID);

        restConseillerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConseiller.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConseiller))
            )
            .andExpect(status().isOk());

        // Validate the Conseiller in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConseillerUpdatableFieldsEquals(partialUpdatedConseiller, getPersistedConseiller(partialUpdatedConseiller));
    }

    @Test
    @Transactional
    void patchNonExistingConseiller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        conseiller.setId(longCount.incrementAndGet());

        // Create the Conseiller
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConseillerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, conseillerDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(conseillerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conseiller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConseiller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        conseiller.setId(longCount.incrementAndGet());

        // Create the Conseiller
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConseillerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(conseillerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conseiller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConseiller() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        conseiller.setId(longCount.incrementAndGet());

        // Create the Conseiller
        ConseillerDTO conseillerDTO = conseillerMapper.toDto(conseiller);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConseillerMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(conseillerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Conseiller in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteConseiller() throws Exception {
        // Initialize the database
        insertedConseiller = conseillerRepository.saveAndFlush(conseiller);
        conseillerRepository.save(conseiller);
        conseillerSearchRepository.save(conseiller);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the conseiller
        restConseillerMockMvc
            .perform(delete(ENTITY_API_URL_ID, conseiller.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(conseillerSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchConseiller() throws Exception {
        // Initialize the database
        insertedConseiller = conseillerRepository.saveAndFlush(conseiller);
        conseillerSearchRepository.save(conseiller);

        // Search the conseiller
        restConseillerMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + conseiller.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conseiller.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].specialite").value(hasItem(DEFAULT_SPECIALITE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].certifie").value(hasItem(DEFAULT_CERTIFIE.booleanValue())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].passwordHash").value(hasItem(DEFAULT_PASSWORD_HASH)))
            .andExpect(jsonPath("$.[*].keycloakId").value(hasItem(DEFAULT_KEYCLOAK_ID)));
    }

    protected long getRepositoryCount() {
        return conseillerRepository.count();
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

    protected Conseiller getPersistedConseiller(Conseiller conseiller) {
        return conseillerRepository.findById(conseiller.getId()).orElseThrow();
    }

    protected void assertPersistedConseillerToMatchAllProperties(Conseiller expectedConseiller) {
        assertConseillerAllPropertiesEquals(expectedConseiller, getPersistedConseiller(expectedConseiller));
    }

    protected void assertPersistedConseillerToMatchUpdatableProperties(Conseiller expectedConseiller) {
        assertConseillerAllUpdatablePropertiesEquals(expectedConseiller, getPersistedConseiller(expectedConseiller));
    }
}
