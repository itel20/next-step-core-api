package com.nextstepsenegal.app.web.rest;

import static com.nextstepsenegal.app.domain.PublicationAsserts.*;
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
import com.nextstepsenegal.app.domain.Publication;
import com.nextstepsenegal.app.domain.enumeration.UserType;
import com.nextstepsenegal.app.repository.PublicationRepository;
import com.nextstepsenegal.app.repository.search.PublicationSearchRepository;
import com.nextstepsenegal.app.service.dto.PublicationDTO;
import com.nextstepsenegal.app.service.mapper.PublicationMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PublicationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PublicationResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Long DEFAULT_AUTHOR_ID = 1L;
    private static final Long UPDATED_AUTHOR_ID = 2L;

    private static final UserType DEFAULT_AUTHOR_TYPE = UserType.ETUDIANT;
    private static final UserType UPDATED_AUTHOR_TYPE = UserType.ELEVE;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/publications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/publications/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PublicationMapper publicationMapper;

    @Autowired
    private PublicationSearchRepository publicationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPublicationMockMvc;

    private Publication publication;

    private Publication insertedPublication;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Publication createEntity() {
        return new Publication()
            .content(DEFAULT_CONTENT)
            .authorId(DEFAULT_AUTHOR_ID)
            .authorType(DEFAULT_AUTHOR_TYPE)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Publication createUpdatedEntity() {
        return new Publication()
            .content(UPDATED_CONTENT)
            .authorId(UPDATED_AUTHOR_ID)
            .authorType(UPDATED_AUTHOR_TYPE)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    public void initTest() {
        publication = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPublication != null) {
            publicationRepository.delete(insertedPublication);
            publicationSearchRepository.delete(insertedPublication);
            insertedPublication = null;
        }
    }

    @Test
    @Transactional
    void createPublication() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        // Create the Publication
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);
        var returnedPublicationDTO = om.readValue(
            restPublicationMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PublicationDTO.class
        );

        // Validate the Publication in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPublication = publicationMapper.toEntity(returnedPublicationDTO);
        assertPublicationUpdatableFieldsEquals(returnedPublication, getPersistedPublication(returnedPublication));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPublication = returnedPublication;
    }

    @Test
    @Transactional
    void createPublicationWithExistingId() throws Exception {
        // Create the Publication with an existing ID
        publication.setId(1L);
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPublicationMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publication in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAuthorIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        // set the field null
        publication.setAuthorId(null);

        // Create the Publication, which fails.
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);

        restPublicationMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAuthorTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        // set the field null
        publication.setAuthorType(null);

        // Create the Publication, which fails.
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);

        restPublicationMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        // set the field null
        publication.setCreatedAt(null);

        // Create the Publication, which fails.
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);

        restPublicationMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPublications() throws Exception {
        // Initialize the database
        insertedPublication = publicationRepository.saveAndFlush(publication);

        // Get all the publicationList
        restPublicationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publication.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].authorId").value(hasItem(DEFAULT_AUTHOR_ID.intValue())))
            .andExpect(jsonPath("$.[*].authorType").value(hasItem(DEFAULT_AUTHOR_TYPE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPublication() throws Exception {
        // Initialize the database
        insertedPublication = publicationRepository.saveAndFlush(publication);

        // Get the publication
        restPublicationMockMvc
            .perform(get(ENTITY_API_URL_ID, publication.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(publication.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.authorId").value(DEFAULT_AUTHOR_ID.intValue()))
            .andExpect(jsonPath("$.authorType").value(DEFAULT_AUTHOR_TYPE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPublication() throws Exception {
        // Get the publication
        restPublicationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPublication() throws Exception {
        // Initialize the database
        insertedPublication = publicationRepository.saveAndFlush(publication);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicationSearchRepository.save(publication);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());

        // Update the publication
        Publication updatedPublication = publicationRepository.findById(publication.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPublication are not directly saved in db
        em.detach(updatedPublication);
        updatedPublication
            .content(UPDATED_CONTENT)
            .authorId(UPDATED_AUTHOR_ID)
            .authorType(UPDATED_AUTHOR_TYPE)
            .createdAt(UPDATED_CREATED_AT);
        PublicationDTO publicationDTO = publicationMapper.toDto(updatedPublication);

        restPublicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publicationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Publication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPublicationToMatchAllProperties(updatedPublication);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Publication> publicationSearchList = Streamable.of(publicationSearchRepository.findAll()).toList();
                Publication testPublicationSearch = publicationSearchList.get(searchDatabaseSizeAfter - 1);

                assertPublicationAllPropertiesEquals(testPublicationSearch, updatedPublication);
            });
    }

    @Test
    @Transactional
    void putNonExistingPublication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        publication.setId(longCount.incrementAndGet());

        // Create the Publication
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publicationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPublication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        publication.setId(longCount.incrementAndGet());

        // Create the Publication
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPublication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        publication.setId(longCount.incrementAndGet());

        // Create the Publication
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Publication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePublicationWithPatch() throws Exception {
        // Initialize the database
        insertedPublication = publicationRepository.saveAndFlush(publication);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publication using partial update
        Publication partialUpdatedPublication = new Publication();
        partialUpdatedPublication.setId(publication.getId());

        partialUpdatedPublication.content(UPDATED_CONTENT).authorId(UPDATED_AUTHOR_ID).authorType(UPDATED_AUTHOR_TYPE);

        restPublicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublication.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublication))
            )
            .andExpect(status().isOk());

        // Validate the Publication in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPublication, publication),
            getPersistedPublication(publication)
        );
    }

    @Test
    @Transactional
    void fullUpdatePublicationWithPatch() throws Exception {
        // Initialize the database
        insertedPublication = publicationRepository.saveAndFlush(publication);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publication using partial update
        Publication partialUpdatedPublication = new Publication();
        partialUpdatedPublication.setId(publication.getId());

        partialUpdatedPublication
            .content(UPDATED_CONTENT)
            .authorId(UPDATED_AUTHOR_ID)
            .authorType(UPDATED_AUTHOR_TYPE)
            .createdAt(UPDATED_CREATED_AT);

        restPublicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublication.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublication))
            )
            .andExpect(status().isOk());

        // Validate the Publication in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicationUpdatableFieldsEquals(partialUpdatedPublication, getPersistedPublication(partialUpdatedPublication));
    }

    @Test
    @Transactional
    void patchNonExistingPublication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        publication.setId(longCount.incrementAndGet());

        // Create the Publication
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, publicationDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPublication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        publication.setId(longCount.incrementAndGet());

        // Create the Publication
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Publication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPublication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        publication.setId(longCount.incrementAndGet());

        // Create the Publication
        PublicationDTO publicationDTO = publicationMapper.toDto(publication);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(publicationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Publication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePublication() throws Exception {
        // Initialize the database
        insertedPublication = publicationRepository.saveAndFlush(publication);
        publicationRepository.save(publication);
        publicationSearchRepository.save(publication);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the publication
        restPublicationMockMvc
            .perform(delete(ENTITY_API_URL_ID, publication.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPublication() throws Exception {
        // Initialize the database
        insertedPublication = publicationRepository.saveAndFlush(publication);
        publicationSearchRepository.save(publication);

        // Search the publication
        restPublicationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + publication.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publication.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].authorId").value(hasItem(DEFAULT_AUTHOR_ID.intValue())))
            .andExpect(jsonPath("$.[*].authorType").value(hasItem(DEFAULT_AUTHOR_TYPE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return publicationRepository.count();
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

    protected Publication getPersistedPublication(Publication publication) {
        return publicationRepository.findById(publication.getId()).orElseThrow();
    }

    protected void assertPersistedPublicationToMatchAllProperties(Publication expectedPublication) {
        assertPublicationAllPropertiesEquals(expectedPublication, getPersistedPublication(expectedPublication));
    }

    protected void assertPersistedPublicationToMatchUpdatableProperties(Publication expectedPublication) {
        assertPublicationAllUpdatablePropertiesEquals(expectedPublication, getPersistedPublication(expectedPublication));
    }
}
