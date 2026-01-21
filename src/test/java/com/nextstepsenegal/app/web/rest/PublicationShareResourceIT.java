package com.nextstepsenegal.app.web.rest;

import static com.nextstepsenegal.app.domain.PublicationShareAsserts.*;
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
import com.nextstepsenegal.app.domain.PublicationShare;
import com.nextstepsenegal.app.domain.enumeration.UserType;
import com.nextstepsenegal.app.repository.PublicationShareRepository;
import com.nextstepsenegal.app.repository.search.PublicationShareSearchRepository;
import com.nextstepsenegal.app.service.dto.PublicationShareDTO;
import com.nextstepsenegal.app.service.mapper.PublicationShareMapper;
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
 * Integration tests for the {@link PublicationShareResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PublicationShareResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final UserType DEFAULT_USER_TYPE = UserType.ETUDIANT;
    private static final UserType UPDATED_USER_TYPE = UserType.ELEVE;

    private static final Instant DEFAULT_SHARED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SHARED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/publication-shares";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/publication-shares/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PublicationShareRepository publicationShareRepository;

    @Autowired
    private PublicationShareMapper publicationShareMapper;

    @Autowired
    private PublicationShareSearchRepository publicationShareSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPublicationShareMockMvc;

    private PublicationShare publicationShare;

    private PublicationShare insertedPublicationShare;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PublicationShare createEntity() {
        return new PublicationShare().userId(DEFAULT_USER_ID).userType(DEFAULT_USER_TYPE).sharedAt(DEFAULT_SHARED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PublicationShare createUpdatedEntity() {
        return new PublicationShare().userId(UPDATED_USER_ID).userType(UPDATED_USER_TYPE).sharedAt(UPDATED_SHARED_AT);
    }

    @BeforeEach
    public void initTest() {
        publicationShare = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPublicationShare != null) {
            publicationShareRepository.delete(insertedPublicationShare);
            publicationShareSearchRepository.delete(insertedPublicationShare);
            insertedPublicationShare = null;
        }
    }

    @Test
    @Transactional
    void createPublicationShare() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        // Create the PublicationShare
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);
        var returnedPublicationShareDTO = om.readValue(
            restPublicationShareMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(publicationShareDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PublicationShareDTO.class
        );

        // Validate the PublicationShare in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPublicationShare = publicationShareMapper.toEntity(returnedPublicationShareDTO);
        assertPublicationShareUpdatableFieldsEquals(returnedPublicationShare, getPersistedPublicationShare(returnedPublicationShare));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPublicationShare = returnedPublicationShare;
    }

    @Test
    @Transactional
    void createPublicationShareWithExistingId() throws Exception {
        // Create the PublicationShare with an existing ID
        publicationShare.setId(1L);
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPublicationShareMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicationShare in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        // set the field null
        publicationShare.setUserId(null);

        // Create the PublicationShare, which fails.
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);

        restPublicationShareMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUserTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        // set the field null
        publicationShare.setUserType(null);

        // Create the PublicationShare, which fails.
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);

        restPublicationShareMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSharedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        // set the field null
        publicationShare.setSharedAt(null);

        // Create the PublicationShare, which fails.
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);

        restPublicationShareMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPublicationShares() throws Exception {
        // Initialize the database
        insertedPublicationShare = publicationShareRepository.saveAndFlush(publicationShare);

        // Get all the publicationShareList
        restPublicationShareMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publicationShare.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].userType").value(hasItem(DEFAULT_USER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].sharedAt").value(hasItem(DEFAULT_SHARED_AT.toString())));
    }

    @Test
    @Transactional
    void getPublicationShare() throws Exception {
        // Initialize the database
        insertedPublicationShare = publicationShareRepository.saveAndFlush(publicationShare);

        // Get the publicationShare
        restPublicationShareMockMvc
            .perform(get(ENTITY_API_URL_ID, publicationShare.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(publicationShare.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.userType").value(DEFAULT_USER_TYPE.toString()))
            .andExpect(jsonPath("$.sharedAt").value(DEFAULT_SHARED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPublicationShare() throws Exception {
        // Get the publicationShare
        restPublicationShareMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPublicationShare() throws Exception {
        // Initialize the database
        insertedPublicationShare = publicationShareRepository.saveAndFlush(publicationShare);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicationShareSearchRepository.save(publicationShare);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());

        // Update the publicationShare
        PublicationShare updatedPublicationShare = publicationShareRepository.findById(publicationShare.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPublicationShare are not directly saved in db
        em.detach(updatedPublicationShare);
        updatedPublicationShare.userId(UPDATED_USER_ID).userType(UPDATED_USER_TYPE).sharedAt(UPDATED_SHARED_AT);
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(updatedPublicationShare);

        restPublicationShareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publicationShareDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isOk());

        // Validate the PublicationShare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPublicationShareToMatchAllProperties(updatedPublicationShare);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PublicationShare> publicationShareSearchList = Streamable.of(publicationShareSearchRepository.findAll()).toList();
                PublicationShare testPublicationShareSearch = publicationShareSearchList.get(searchDatabaseSizeAfter - 1);

                assertPublicationShareAllPropertiesEquals(testPublicationShareSearch, updatedPublicationShare);
            });
    }

    @Test
    @Transactional
    void putNonExistingPublicationShare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        publicationShare.setId(longCount.incrementAndGet());

        // Create the PublicationShare
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublicationShareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publicationShareDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicationShare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPublicationShare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        publicationShare.setId(longCount.incrementAndGet());

        // Create the PublicationShare
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationShareMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicationShare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPublicationShare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        publicationShare.setId(longCount.incrementAndGet());

        // Create the PublicationShare
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationShareMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PublicationShare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePublicationShareWithPatch() throws Exception {
        // Initialize the database
        insertedPublicationShare = publicationShareRepository.saveAndFlush(publicationShare);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicationShare using partial update
        PublicationShare partialUpdatedPublicationShare = new PublicationShare();
        partialUpdatedPublicationShare.setId(publicationShare.getId());

        partialUpdatedPublicationShare.userId(UPDATED_USER_ID).sharedAt(UPDATED_SHARED_AT);

        restPublicationShareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublicationShare.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublicationShare))
            )
            .andExpect(status().isOk());

        // Validate the PublicationShare in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicationShareUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPublicationShare, publicationShare),
            getPersistedPublicationShare(publicationShare)
        );
    }

    @Test
    @Transactional
    void fullUpdatePublicationShareWithPatch() throws Exception {
        // Initialize the database
        insertedPublicationShare = publicationShareRepository.saveAndFlush(publicationShare);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicationShare using partial update
        PublicationShare partialUpdatedPublicationShare = new PublicationShare();
        partialUpdatedPublicationShare.setId(publicationShare.getId());

        partialUpdatedPublicationShare.userId(UPDATED_USER_ID).userType(UPDATED_USER_TYPE).sharedAt(UPDATED_SHARED_AT);

        restPublicationShareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublicationShare.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublicationShare))
            )
            .andExpect(status().isOk());

        // Validate the PublicationShare in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicationShareUpdatableFieldsEquals(
            partialUpdatedPublicationShare,
            getPersistedPublicationShare(partialUpdatedPublicationShare)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPublicationShare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        publicationShare.setId(longCount.incrementAndGet());

        // Create the PublicationShare
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublicationShareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, publicationShareDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicationShare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPublicationShare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        publicationShare.setId(longCount.incrementAndGet());

        // Create the PublicationShare
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationShareMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicationShare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPublicationShare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        publicationShare.setId(longCount.incrementAndGet());

        // Create the PublicationShare
        PublicationShareDTO publicationShareDTO = publicationShareMapper.toDto(publicationShare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationShareMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicationShareDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PublicationShare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePublicationShare() throws Exception {
        // Initialize the database
        insertedPublicationShare = publicationShareRepository.saveAndFlush(publicationShare);
        publicationShareRepository.save(publicationShare);
        publicationShareSearchRepository.save(publicationShare);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the publicationShare
        restPublicationShareMockMvc
            .perform(delete(ENTITY_API_URL_ID, publicationShare.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationShareSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPublicationShare() throws Exception {
        // Initialize the database
        insertedPublicationShare = publicationShareRepository.saveAndFlush(publicationShare);
        publicationShareSearchRepository.save(publicationShare);

        // Search the publicationShare
        restPublicationShareMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + publicationShare.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publicationShare.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].userType").value(hasItem(DEFAULT_USER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].sharedAt").value(hasItem(DEFAULT_SHARED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return publicationShareRepository.count();
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

    protected PublicationShare getPersistedPublicationShare(PublicationShare publicationShare) {
        return publicationShareRepository.findById(publicationShare.getId()).orElseThrow();
    }

    protected void assertPersistedPublicationShareToMatchAllProperties(PublicationShare expectedPublicationShare) {
        assertPublicationShareAllPropertiesEquals(expectedPublicationShare, getPersistedPublicationShare(expectedPublicationShare));
    }

    protected void assertPersistedPublicationShareToMatchUpdatableProperties(PublicationShare expectedPublicationShare) {
        assertPublicationShareAllUpdatablePropertiesEquals(
            expectedPublicationShare,
            getPersistedPublicationShare(expectedPublicationShare)
        );
    }
}
