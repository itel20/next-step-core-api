package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.domain.enumeration.UserType;
import com.nextstepsenegal.app.repository.PublicationShareRepository;
import com.nextstepsenegal.app.service.PublicationShareService;
import com.nextstepsenegal.app.service.dto.PublicationShareDTO;
import com.nextstepsenegal.app.web.rest.errors.BadRequestAlertException;
import com.nextstepsenegal.app.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.nextstepsenegal.app.domain.PublicationShare}.
 */
@RestController
@RequestMapping("/api/publication-shares")
public class PublicationShareResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationShareResource.class);

    private static final String ENTITY_NAME = "userManagementServicePublicationShare";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PublicationShareService publicationShareService;

    private final PublicationShareRepository publicationShareRepository;

    public PublicationShareResource(
        PublicationShareService publicationShareService,
        PublicationShareRepository publicationShareRepository
    ) {
        this.publicationShareService = publicationShareService;
        this.publicationShareRepository = publicationShareRepository;
    }

    /**
     * {@code POST  /publications/{id}/share} : Share a publication.
     *
     * @param id the id of the publication to share
     * @param userId the user sharing the publication
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new PublicationShareDTO
     */
    @PostMapping("/publications/{id}/share/{userId}/{type}")
    public ResponseEntity<PublicationShareDTO> partager(
        @PathVariable Long id,
        @PathVariable Long userId,
        @PathVariable UserType type
    ) {
        LOG.debug("REST request to share Publication {} by {}", id, userId, type);

        PublicationShareDTO result = publicationShareService.partagerPublication(
            id,
            userId,
            type
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result);
    }
    /**
     * {@code GET /publications/{id}/shares/count} : get total shares of a publication.
     *
     * @param id the id of the publication
     * @return the number of shares
     */
    @GetMapping("/publications/{id}/shares/count")
    public ResponseEntity<Long> getSharesCount(@PathVariable Long id) {
        LOG.debug("REST request to get shares count for Publication {}", id);
        long count = publicationShareService.countSharesByPublication(id);
        return ResponseEntity.ok(count);
    }




    /**
     * {@code PUT  /publication-shares/:id} : Updates an existing publicationShare.
     *
     * @param id the id of the publicationShareDTO to save.
     * @param publicationShareDTO the publicationShareDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publicationShareDTO,
     * or with status {@code 400 (Bad Request)} if the publicationShareDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the publicationShareDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PublicationShareDTO> updatePublicationShare(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PublicationShareDTO publicationShareDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PublicationShare : {}, {}", id, publicationShareDTO);
        if (publicationShareDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publicationShareDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publicationShareRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        publicationShareDTO = publicationShareService.update(publicationShareDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publicationShareDTO.getId().toString()))
            .body(publicationShareDTO);
    }

    /**
     * {@code PATCH  /publication-shares/:id} : Partial updates given fields of an existing publicationShare, field will ignore if it is null
     *
     * @param id the id of the publicationShareDTO to save.
     * @param publicationShareDTO the publicationShareDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publicationShareDTO,
     * or with status {@code 400 (Bad Request)} if the publicationShareDTO is not valid,
     * or with status {@code 404 (Not Found)} if the publicationShareDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the publicationShareDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PublicationShareDTO> partialUpdatePublicationShare(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PublicationShareDTO publicationShareDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PublicationShare partially : {}, {}", id, publicationShareDTO);
        if (publicationShareDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publicationShareDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publicationShareRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PublicationShareDTO> result = publicationShareService.partialUpdate(publicationShareDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publicationShareDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /publication-shares} : get all the publicationShares.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of publicationShares in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PublicationShareDTO>> getAllPublicationShares(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of PublicationShares");
        Page<PublicationShareDTO> page = publicationShareService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /publication-shares/:id} : get the "id" publicationShare.
     *
     * @param id the id of the publicationShareDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the publicationShareDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublicationShareDTO> getPublicationShare(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PublicationShare : {}", id);
        Optional<PublicationShareDTO> publicationShareDTO = publicationShareService.findOne(id);
        return ResponseUtil.wrapOrNotFound(publicationShareDTO);
    }

    /**
     * {@code DELETE  /publication-shares/:id} : delete the "id" publicationShare.
     *
     * @param id the id of the publicationShareDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublicationShare(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PublicationShare : {}", id);
        publicationShareService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /publication-shares/_search?query=:query} : search for the publicationShare corresponding
     * to the query.
     *
     * @param query the query of the publicationShare search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PublicationShareDTO>> searchPublicationShares(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of PublicationShares for query {}", query);
        try {
            Page<PublicationShareDTO> page = publicationShareService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
