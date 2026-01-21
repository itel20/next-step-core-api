package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.domain.Publication;
import com.nextstepsenegal.app.domain.PublicationLike;
import com.nextstepsenegal.app.domain.enumeration.UserType;
import com.nextstepsenegal.app.repository.PublicationLikeRepository;
import com.nextstepsenegal.app.repository.PublicationRepository;
import com.nextstepsenegal.app.repository.search.PublicationLikeSearchRepository;
import com.nextstepsenegal.app.service.dto.PublicationLikeDTO;
import com.nextstepsenegal.app.service.mapper.PublicationLikeMapper;

import java.time.Instant;
import java.util.Optional;

import com.nextstepsenegal.app.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.nextstepsenegal.app.domain.PublicationLike}.
 */
@Service
@Transactional
public class PublicationLikeService {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationLikeService.class);

    private final PublicationLikeRepository publicationLikeRepository;

    private final PublicationLikeMapper publicationLikeMapper;

    private final PublicationLikeSearchRepository publicationLikeSearchRepository;

    private final PublicationRepository publicationRepository;

    public PublicationLikeService(
        PublicationLikeRepository publicationLikeRepository,
        PublicationLikeMapper publicationLikeMapper,
        PublicationLikeSearchRepository publicationLikeSearchRepository,
        PublicationRepository publicationRepository
    ) {
        this.publicationLikeRepository = publicationLikeRepository;
        this.publicationLikeMapper = publicationLikeMapper;
        this.publicationLikeSearchRepository = publicationLikeSearchRepository;
        this.publicationRepository = publicationRepository;
    }

    /**
     * Save a publicationLike.
     *
     *
     * @return the persisted entity.
     */
    @Transactional
    public Optional<PublicationLikeDTO> toggleLike(
        Long publicationId,
        Long userId,
        UserType userType
    ) {
        LOG.debug(
            "Request to toggle like on Publication {} by user {} ({})",
            publicationId,
            userId,
            userType
        );

        //  Vérifier la publication
        Publication publication = publicationRepository
            .findById(publicationId)
            .orElseThrow(() -> new BadRequestAlertException(
                "Publication not found",
                "publication",
                "idnotfound"
            ));

        //  Chercher un like existant
        Optional<PublicationLike> existingLike =
            publicationLikeRepository
                .findOneByPublicationIdAndUserIdAndUserType(
                    publicationId,
                    userId,
                    userType
                );

        // Si existe → UNLIKE
        if (existingLike.isPresent()) {
            publicationLikeRepository.delete(existingLike.get());
            publicationLikeSearchRepository.deleteById(existingLike.get().getId());
            return Optional.empty(); // unlike
        }

        // Sinon → LIKE
        PublicationLike publicationLike = new PublicationLike();
        publicationLike.setPublication(publication);
        publicationLike.setUserId(userId);
        publicationLike.setUserType(userType);
        publicationLike.setLikedAt(Instant.now());

        publicationLike = publicationLikeRepository.save(publicationLike);
        publicationLikeSearchRepository.index(publicationLike);

        return Optional.of(publicationLikeMapper.toDto(publicationLike));
    }
    @Transactional(readOnly = true)
    public long countLikesByPublication(Long publicationId) {
        LOG.debug("Request to count likes for Publication {}", publicationId);
        return publicationLikeRepository.countByPublicationId(publicationId);
    }



    /**
     * Update a publicationLike.
     *
     * @param publicationLikeDTO the entity to save.
     * @return the persisted entity.
     */
    public PublicationLikeDTO update(PublicationLikeDTO publicationLikeDTO) {
        LOG.debug("Request to update PublicationLike : {}", publicationLikeDTO);
        PublicationLike publicationLike = publicationLikeMapper.toEntity(publicationLikeDTO);
        publicationLike = publicationLikeRepository.save(publicationLike);
        publicationLikeSearchRepository.index(publicationLike);
        return publicationLikeMapper.toDto(publicationLike);
    }

    /**
     * Partially update a publicationLike.
     *
     * @param publicationLikeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PublicationLikeDTO> partialUpdate(PublicationLikeDTO publicationLikeDTO) {
        LOG.debug("Request to partially update PublicationLike : {}", publicationLikeDTO);

        return publicationLikeRepository
            .findById(publicationLikeDTO.getId())
            .map(existingPublicationLike -> {
                publicationLikeMapper.partialUpdate(existingPublicationLike, publicationLikeDTO);

                return existingPublicationLike;
            })
            .map(publicationLikeRepository::save)
            .map(savedPublicationLike -> {
                publicationLikeSearchRepository.index(savedPublicationLike);
                return savedPublicationLike;
            })
            .map(publicationLikeMapper::toDto);
    }

    /**
     * Get all the publicationLikes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PublicationLikeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PublicationLikes");
        return publicationLikeRepository.findAll(pageable).map(publicationLikeMapper::toDto);
    }

    /**
     * Get one publicationLike by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PublicationLikeDTO> findOne(Long id) {
        LOG.debug("Request to get PublicationLike : {}", id);
        return publicationLikeRepository.findById(id).map(publicationLikeMapper::toDto);
    }

    /**
     * Delete the publicationLike by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PublicationLike : {}", id);
        publicationLikeRepository.deleteById(id);
        publicationLikeSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the publicationLike corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PublicationLikeDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of PublicationLikes for query {}", query);
        return publicationLikeSearchRepository.search(query, pageable).map(publicationLikeMapper::toDto);
    }
}
