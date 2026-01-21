package com.nextstepsenegal.app.service;

import com.nextstepsenegal.app.domain.Publication;
import com.nextstepsenegal.app.domain.enumeration.UserType;
import com.nextstepsenegal.app.repository.PublicationRepository;
import com.nextstepsenegal.app.repository.search.PublicationSearchRepository;
import com.nextstepsenegal.app.service.dto.PublicationDTO;
import com.nextstepsenegal.app.service.mapper.PublicationMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.nextstepsenegal.app.domain.Publication}.
 */
@Service
@Transactional
public class PublicationService {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationService.class);

    private final PublicationRepository publicationRepository;

    private final PublicationMapper publicationMapper;

    private final PublicationSearchRepository publicationSearchRepository;

    public PublicationService(
        PublicationRepository publicationRepository,
        PublicationMapper publicationMapper,
        PublicationSearchRepository publicationSearchRepository
    ) {
        this.publicationRepository = publicationRepository;
        this.publicationMapper = publicationMapper;
        this.publicationSearchRepository = publicationSearchRepository;
    }

    /**
     * Save a publication.
     *
     * @param publicationDTO the entity to save.
     * @return the persisted entity.
     */
    @Transactional
    public PublicationDTO publier(PublicationDTO publicationDTO) {
        LOG.debug("Request to publish Publication : {}", publicationDTO);

        Publication publication = publicationMapper.toEntity(publicationDTO);

        // Date toujours côté serveur
        publication.setCreatedAt(Instant.now());

        // Laisser le front gérer MAIS prévoir fallback
        if (publication.getAuthorId() == null || publication.getAuthorType() == null) {
            // Placeholder temporaire (dev)
            publication.setAuthorId(0L);
            publication.setAuthorType(UserType.CONSEILLER);
        }

        publication = publicationRepository.save(publication);
        publicationSearchRepository.index(publication);

        return publicationMapper.toDto(publication);
    }


    /**
     * Update a publication.
     *
     * @param publicationDTO the entity to save.
     * @return the persisted entity.
     */
    public PublicationDTO update(PublicationDTO publicationDTO) {
        LOG.debug("Request to update Publication : {}", publicationDTO);
        Publication publication = publicationMapper.toEntity(publicationDTO);
        publication = publicationRepository.save(publication);
        publicationSearchRepository.index(publication);
        return publicationMapper.toDto(publication);
    }

    /**
     * Partially update a publication.
     *
     * @param publicationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PublicationDTO> partialUpdate(PublicationDTO publicationDTO) {
        LOG.debug("Request to partially update Publication : {}", publicationDTO);

        return publicationRepository
            .findById(publicationDTO.getId())
            .map(existingPublication -> {
                publicationMapper.partialUpdate(existingPublication, publicationDTO);

                return existingPublication;
            })
            .map(publicationRepository::save)
            .map(savedPublication -> {
                publicationSearchRepository.index(savedPublication);
                return savedPublication;
            })
            .map(publicationMapper::toDto);
    }

    /**
     * Get all the publications.
     *
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PublicationDTO> listerPublications(Pageable pageable) {
        LOG.debug("Request to get all Publications");
        return publicationRepository.findAll(pageable)
            .map(publicationMapper::toDto);
    }



    /**
     * Get one publication by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PublicationDTO> findOne(Long id) {
        LOG.debug("Request to get Publication : {}", id);
        return publicationRepository.findById(id).map(publicationMapper::toDto);
    }

    /**
     * Delete the publication by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Publication : {}", id);
        publicationRepository.deleteById(id);
        publicationSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the publication corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PublicationDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Publications for query {}", query);
        return publicationSearchRepository.search(query, pageable).map(publicationMapper::toDto);
    }
}
