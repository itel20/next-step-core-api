package com.nextstepsenegal.app.web.rest;

import static com.nextstepsenegal.app.domain.PublicationLikeAsserts.*;
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
import com.nextstepsenegal.app.domain.PublicationLike;
import com.nextstepsenegal.app.domain.enumeration.UserType;
import com.nextstepsenegal.app.repository.PublicationLikeRepository;
import com.nextstepsenegal.app.repository.search.PublicationLikeSearchRepository;
import com.nextstepsenegal.app.service.dto.PublicationLikeDTO;
import com.nextstepsenegal.app.service.mapper.PublicationLikeMapper;
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
 * Integration tests for the {@link PublicationLikeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PublicationLikeResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final UserType DEFAULT_USER_TYPE = UserType.ETUDIANT;
    private static final UserType UPDATED_USER_TYPE = UserType.ELEVE;

    private static final Instant DEFAULT_LIKED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LIKED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/publication-likes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/publication-likes/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PublicationLikeRepository publicationLikeRepository;

    @Autowired
    private PublicationLikeMapper publicationLikeMapper;

    @Autowired
    private PublicationLikeSearchRepository publicationLikeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPublicationLikeMockMvc;

    private PublicationLike publicationLike;

    private PublicationLike insertedPublicationLike;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PublicationLike createEntity() {
        return new PublicationLike().userId(DEFAULT_USER_ID).userType(DEFAULT_USER_TYPE).likedAt(DEFAULT_LIKED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PublicationLike createUpdatedEntity() {
        return new PublicationLike().userId(UPDATED_USER_ID).userType(UPDATED_USER_TYPE).likedAt(UPDATED_LIKED_AT);
    }

    @BeforeEach
    public void initTest() {
        publicationLike = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPublicationLike != null) {
            publicationLikeRepository.delete(insertedPublicationLike);
            publicationLikeSearchRepository.delete(insertedPublicationLike);
            insertedPublicationLike = null;
        }
    }

    @Test
    @Transactional
    void createPublicationLike() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        // Create the PublicationLike
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);
        var returnedPublicationLikeDTO = om.readValue(
            restPublicationLikeMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(publicationLikeDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PublicationLikeDTO.class
        );

        // Validate the PublicationLike in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPublicationLike = publicationLikeMapper.toEntity(returnedPublicationLikeDTO);
        assertPublicationLikeUpdatableFieldsEquals(returnedPublicationLike, getPersistedPublicationLike(returnedPublicationLike));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedPublicationLike = returnedPublicationLike;
    }

    @Test
    @Transactional
    void createPublicationLikeWithExistingId() throws Exception {
        // Create the PublicationLike with an existing ID
        publicationLike.setId(1L);
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPublicationLikeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicationLike in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        // set the field null
        publicationLike.setUserId(null);

        // Create the PublicationLike, which fails.
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);

        restPublicationLikeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUserTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        // set the field null
        publicationLike.setUserType(null);

        // Create the PublicationLike, which fails.
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);

        restPublicationLikeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLikedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        // set the field null
        publicationLike.setLikedAt(null);

        // Create the PublicationLike, which fails.
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);

        restPublicationLikeMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPublicationLikes() throws Exception {
        // Initialize the database
        insertedPublicationLike = publicationLikeRepository.saveAndFlush(publicationLike);

        // Get all the publicationLikeList
        restPublicationLikeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publicationLike.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].userType").value(hasItem(DEFAULT_USER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].likedAt").value(hasItem(DEFAULT_LIKED_AT.toString())));
    }

    @Test
    @Transactional
    void getPublicationLike() throws Exception {
        // Initialize the database
        insertedPublicationLike = publicationLikeRepository.saveAndFlush(publicationLike);

        // Get the publicationLike
        restPublicationLikeMockMvc
            .perform(get(ENTITY_API_URL_ID, publicationLike.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(publicationLike.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.userType").value(DEFAULT_USER_TYPE.toString()))
            .andExpect(jsonPath("$.likedAt").value(DEFAULT_LIKED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPublicationLike() throws Exception {
        // Get the publicationLike
        restPublicationLikeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPublicationLike() throws Exception {
        // Initialize the database
        insertedPublicationLike = publicationLikeRepository.saveAndFlush(publicationLike);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicationLikeSearchRepository.save(publicationLike);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());

        // Update the publicationLike
        PublicationLike updatedPublicationLike = publicationLikeRepository.findById(publicationLike.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPublicationLike are not directly saved in db
        em.detach(updatedPublicationLike);
        updatedPublicationLike.userId(UPDATED_USER_ID).userType(UPDATED_USER_TYPE).likedAt(UPDATED_LIKED_AT);
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(updatedPublicationLike);

        restPublicationLikeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publicationLikeDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isOk());

        // Validate the PublicationLike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPublicationLikeToMatchAllProperties(updatedPublicationLike);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<PublicationLike> publicationLikeSearchList = Streamable.of(publicationLikeSearchRepository.findAll()).toList();
                PublicationLike testPublicationLikeSearch = publicationLikeSearchList.get(searchDatabaseSizeAfter - 1);

                assertPublicationLikeAllPropertiesEquals(testPublicationLikeSearch, updatedPublicationLike);
            });
    }

    @Test
    @Transactional
    void putNonExistingPublicationLike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        publicationLike.setId(longCount.incrementAndGet());

        // Create the PublicationLike
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublicationLikeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publicationLikeDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicationLike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPublicationLike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        publicationLike.setId(longCount.incrementAndGet());

        // Create the PublicationLike
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationLikeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicationLike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPublicationLike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        publicationLike.setId(longCount.incrementAndGet());

        // Create the PublicationLike
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationLikeMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PublicationLike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePublicationLikeWithPatch() throws Exception {
        // Initialize the database
        insertedPublicationLike = publicationLikeRepository.saveAndFlush(publicationLike);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicationLike using partial update
        PublicationLike partialUpdatedPublicationLike = new PublicationLike();
        partialUpdatedPublicationLike.setId(publicationLike.getId());

        partialUpdatedPublicationLike.userId(UPDATED_USER_ID).userType(UPDATED_USER_TYPE);

        restPublicationLikeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublicationLike.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublicationLike))
            )
            .andExpect(status().isOk());

        // Validate the PublicationLike in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicationLikeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPublicationLike, publicationLike),
            getPersistedPublicationLike(publicationLike)
        );
    }

    @Test
    @Transactional
    void fullUpdatePublicationLikeWithPatch() throws Exception {
        // Initialize the database
        insertedPublicationLike = publicationLikeRepository.saveAndFlush(publicationLike);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicationLike using partial update
        PublicationLike partialUpdatedPublicationLike = new PublicationLike();
        partialUpdatedPublicationLike.setId(publicationLike.getId());

        partialUpdatedPublicationLike.userId(UPDATED_USER_ID).userType(UPDATED_USER_TYPE).likedAt(UPDATED_LIKED_AT);

        restPublicationLikeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublicationLike.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublicationLike))
            )
            .andExpect(status().isOk());

        // Validate the PublicationLike in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicationLikeUpdatableFieldsEquals(
            partialUpdatedPublicationLike,
            getPersistedPublicationLike(partialUpdatedPublicationLike)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPublicationLike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        publicationLike.setId(longCount.incrementAndGet());

        // Create the PublicationLike
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublicationLikeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, publicationLikeDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicationLike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPublicationLike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        publicationLike.setId(longCount.incrementAndGet());

        // Create the PublicationLike
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationLikeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicationLike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPublicationLike() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        publicationLike.setId(longCount.incrementAndGet());

        // Create the PublicationLike
        PublicationLikeDTO publicationLikeDTO = publicationLikeMapper.toDto(publicationLike);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicationLikeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicationLikeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PublicationLike in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePublicationLike() throws Exception {
        // Initialize the database
        insertedPublicationLike = publicationLikeRepository.saveAndFlush(publicationLike);
        publicationLikeRepository.save(publicationLike);
        publicationLikeSearchRepository.save(publicationLike);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the publicationLike
        restPublicationLikeMockMvc
            .perform(delete(ENTITY_API_URL_ID, publicationLike.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(publicationLikeSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPublicationLike() throws Exception {
        // Initialize the database
        insertedPublicationLike = publicationLikeRepository.saveAndFlush(publicationLike);
        publicationLikeSearchRepository.save(publicationLike);

        // Search the publicationLike
        restPublicationLikeMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + publicationLike.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publicationLike.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].userType").value(hasItem(DEFAULT_USER_TYPE.toString())))
            .andExpect(jsonPath("$.[*].likedAt").value(hasItem(DEFAULT_LIKED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return publicationLikeRepository.count();
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

    protected PublicationLike getPersistedPublicationLike(PublicationLike publicationLike) {
        return publicationLikeRepository.findById(publicationLike.getId()).orElseThrow();
    }

    protected void assertPersistedPublicationLikeToMatchAllProperties(PublicationLike expectedPublicationLike) {
        assertPublicationLikeAllPropertiesEquals(expectedPublicationLike, getPersistedPublicationLike(expectedPublicationLike));
    }

    protected void assertPersistedPublicationLikeToMatchUpdatableProperties(PublicationLike expectedPublicationLike) {
        assertPublicationLikeAllUpdatablePropertiesEquals(expectedPublicationLike, getPersistedPublicationLike(expectedPublicationLike));
    }
}
