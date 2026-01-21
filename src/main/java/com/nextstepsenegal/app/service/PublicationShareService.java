package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.domain.Publication;
import com.nextstepsenegal.app.domain.PublicationShare;
import com.nextstepsenegal.app.domain.enumeration.UserType;
import com.nextstepsenegal.app.repository.PublicationRepository;
import com.nextstepsenegal.app.repository.PublicationShareRepository;
import com.nextstepsenegal.app.repository.search.PublicationShareSearchRepository;
import com.nextstepsenegal.app.service.dto.PublicationShareDTO;
import com.nextstepsenegal.app.service.mapper.PublicationShareMapper;

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
 * Service Implementation for managing {@link com.nextstepsenegal.app.domain.PublicationShare}.
 */
@Service
@Transactional
public class PublicationShareService {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationShareService.class);

    private final PublicationShareRepository publicationShareRepository;

    private final PublicationShareMapper publicationShareMapper;

    private final PublicationShareSearchRepository publicationShareSearchRepository;

    private final PublicationRepository publicationRepository;

    public PublicationShareService(
        PublicationShareRepository publicationShareRepository,
        PublicationShareMapper publicationShareMapper,
        PublicationShareSearchRepository publicationShareSearchRepository,
        PublicationRepository publicationRepository
    ) {
        this.publicationShareRepository = publicationShareRepository;
        this.publicationShareMapper = publicationShareMapper;
        this.publicationShareSearchRepository = publicationShareSearchRepository;
        this.publicationRepository = publicationRepository;
    }

    /**
     * Save a publicationShare.
     *
     * @param publicationId
     * @param userId
     * @return the persisted entity.
     */
    @Transactional
    public PublicationShareDTO partagerPublication(
        Long publicationId,
        Long userId,
        UserType type
    ) {
        LOG.debug(
            "Request to share Publication {} by user {} ({})",
            publicationId,
            userId,
            type
        );


        //  Vérifier la publication
        Publication publication = publicationRepository
            .findById(publicationId)
            .orElseThrow(() -> new BadRequestAlertException(
                "Publication not found",
                "publication",
                "idnotfound"
            ));

        // Créer l'entité PublicationShare
        PublicationShare publicationShare = new PublicationShare();


        publicationShare.setPublication(publication);
        publicationShare.setUserId(userId);
        publicationShare.setUserType(type);
        publicationShare.setSharedAt(Instant.now());

        // Sauvegarder
        publicationShare = publicationShareRepository.save(publicationShare);

        //  Indexer (optionnel – Elasticsearch)
        publicationShareSearchRepository.index(publicationShare);

        // Retour DTO
        return publicationShareMapper.toDto(publicationShare);
    }

    @Transactional(readOnly = true)
    public long countSharesByPublication(Long publicationId) {
        LOG.debug("Request to count shares for Publication {}", publicationId);
        return publicationShareRepository.countByPublicationId(publicationId);
    }



    /**
     * Update a publicationShare.
     *
     * @param publicationShareDTO the entity to save.
     * @return the persisted entity.
     */
    public PublicationShareDTO update(PublicationShareDTO publicationShareDTO) {
        LOG.debug("Request to update PublicationShare : {}", publicationShareDTO);
        PublicationShare publicationShare = publicationShareMapper.toEntity(publicationShareDTO);
        publicationShare = publicationShareRepository.save(publicationShare);
        publicationShareSearchRepository.index(publicationShare);
        return publicationShareMapper.toDto(publicationShare);
    }

    /**
     * Partially update a publicationShare.
     *
     * @param publicationShareDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PublicationShareDTO> partialUpdate(PublicationShareDTO publicationShareDTO) {
        LOG.debug("Request to partially update PublicationShare : {}", publicationShareDTO);

        return publicationShareRepository
            .findById(publicationShareDTO.getId())
            .map(existingPublicationShare -> {
                publicationShareMapper.partialUpdate(existingPublicationShare, publicationShareDTO);

                return existingPublicationShare;
            })
            .map(publicationShareRepository::save)
            .map(savedPublicationShare -> {
                publicationShareSearchRepository.index(savedPublicationShare);
                return savedPublicationShare;
            })
            .map(publicationShareMapper::toDto);
    }

    /**
     * Get all the publicationShares.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PublicationShareDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PublicationShares");
        return publicationShareRepository.findAll(pageable).map(publicationShareMapper::toDto);
    }

    /**
     * Get one publicationShare by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PublicationShareDTO> findOne(Long id) {
        LOG.debug("Request to get PublicationShare : {}", id);
        return publicationShareRepository.findById(id).map(publicationShareMapper::toDto);
    }

    /**
     * Delete the publicationShare by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PublicationShare : {}", id);
        publicationShareRepository.deleteById(id);
        publicationShareSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the publicationShare corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PublicationShareDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of PublicationShares for query {}", query);
        return publicationShareSearchRepository.search(query, pageable).map(publicationShareMapper::toDto);
    }
}
