package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.domain.enumeration.UserType;
import com.nextstepsenegal.app.repository.PublicationLikeRepository;
import com.nextstepsenegal.app.service.PublicationLikeService;
import com.nextstepsenegal.app.service.dto.PublicationLikeDTO;
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
 * REST controller for managing {@link com.nextstepsenegal.app.domain.PublicationLike}.
 */
@RestController
@RequestMapping("/api/publication-likes")
public class PublicationLikeResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationLikeResource.class);

    private static final String ENTITY_NAME = "userManagementServicePublicationLike";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PublicationLikeService publicationLikeService;

    private final PublicationLikeRepository publicationLikeRepository;

    public PublicationLikeResource(PublicationLikeService publicationLikeService, PublicationLikeRepository publicationLikeRepository) {
        this.publicationLikeService = publicationLikeService;
        this.publicationLikeRepository = publicationLikeRepository;
    }

    @PostMapping("/publications/{id}/like/{userId}/{type}")
    public ResponseEntity<PublicationLikeDTO> toggleLike(
        @PathVariable Long id,
        @PathVariable Long userId,
        @PathVariable UserType type
    ) {
        Optional<PublicationLikeDTO> result =
            publicationLikeService.toggleLike(
                id,
                userId,
                type
            );

        return result
            .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
            .orElseGet(() -> ResponseEntity.noContent().build());
    }


    /**
     * {@code PUT  /publication-likes/:id} : Updates an existing publicationLike.
     *
     * @param id the id of the publicationLikeDTO to save.
     * @param publicationLikeDTO the publicationLikeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publicationLikeDTO,
     * or with status {@code 400 (Bad Request)} if the publicationLikeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the publicationLikeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PublicationLikeDTO> updatePublicationLike(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PublicationLikeDTO publicationLikeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PublicationLike : {}, {}", id, publicationLikeDTO);
        if (publicationLikeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publicationLikeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publicationLikeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        publicationLikeDTO = publicationLikeService.update(publicationLikeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publicationLikeDTO.getId().toString()))
            .body(publicationLikeDTO);
    }

    /**
     * {@code PATCH  /publication-likes/:id} : Partial updates given fields of an existing publicationLike, field will ignore if it is null
     *
     * @param id the id of the publicationLikeDTO to save.
     * @param publicationLikeDTO the publicationLikeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publicationLikeDTO,
     * or with status {@code 400 (Bad Request)} if the publicationLikeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the publicationLikeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the publicationLikeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PublicationLikeDTO> partialUpdatePublicationLike(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PublicationLikeDTO publicationLikeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PublicationLike partially : {}, {}", id, publicationLikeDTO);
        if (publicationLikeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publicationLikeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publicationLikeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PublicationLikeDTO> result = publicationLikeService.partialUpdate(publicationLikeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publicationLikeDTO.getId().toString())
        );
    }
    /**
     * {@code GET /publications/{id}/likes/count} : get total likes of a publication.
     *
     * @param id the id of the publication
     * @return the number of likes
     */
    @GetMapping("/publications/{id}/likes/count")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long id) {
        LOG.debug("REST request to get likes count for Publication {}", id);
        long count = publicationLikeService.countLikesByPublication(id);
        return ResponseEntity.ok(count);
    }


    /**
     * {@code GET  /publication-likes} : get all the publicationLikes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of publicationLikes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PublicationLikeDTO>> getAllPublicationLikes(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of PublicationLikes");
        Page<PublicationLikeDTO> page = publicationLikeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /publication-likes/:id} : get the "id" publicationLike.
     *
     * @param id the id of the publicationLikeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the publicationLikeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublicationLikeDTO> getPublicationLike(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PublicationLike : {}", id);
        Optional<PublicationLikeDTO> publicationLikeDTO = publicationLikeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(publicationLikeDTO);
    }

    /**
     * {@code DELETE  /publication-likes/:id} : delete the "id" publicationLike.
     *
     * @param id the id of the publicationLikeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublicationLike(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PublicationLike : {}", id);
        publicationLikeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /publication-likes/_search?query=:query} : search for the publicationLike corresponding
     * to the query.
     *
     * @param query the query of the publicationLike search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PublicationLikeDTO>> searchPublicationLikes(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of PublicationLikes for query {}", query);
        try {
            Page<PublicationLikeDTO> page = publicationLikeService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
