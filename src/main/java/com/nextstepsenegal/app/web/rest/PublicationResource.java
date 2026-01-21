package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.repository.PublicationRepository;
import com.nextstepsenegal.app.service.PublicationService;
import com.nextstepsenegal.app.service.dto.PublicationDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.nextstepsenegal.app.domain.Publication}.
 */
@RestController
@RequestMapping("/api/publications")
public class PublicationResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationResource.class);

    private static final String ENTITY_NAME = "userManagementServicePublication";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PublicationService publicationService;

    private final PublicationRepository publicationRepository;

    public PublicationResource(PublicationService publicationService, PublicationRepository publicationRepository) {
        this.publicationService = publicationService;
        this.publicationRepository = publicationRepository;
    }

    /**
     * {@code POST  /publications} : Create a new publication.
     *
     * @param publicationDTO the publicationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new publicationDTO, or with status {@code 400 (Bad Request)} if the publication has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PublicationDTO> createPublication(@Valid @RequestBody PublicationDTO publicationDTO) throws URISyntaxException {
        LOG.debug("REST request to save Publication : {}", publicationDTO);
        if (publicationDTO.getId() != null) {
            throw new BadRequestAlertException("A new publication cannot already have an ID", ENTITY_NAME, "idexists");
        }
        publicationDTO = publicationService.publier(publicationDTO);
        return ResponseEntity.created(new URI("/api/publications/" + publicationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, publicationDTO.getId().toString()))
            .body(publicationDTO);
    }

    /**
     * {@code PUT  /publications/:id} : Updates an existing publication.
     *
     * @param id the id of the publicationDTO to save.
     * @param publicationDTO the publicationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publicationDTO,
     * or with status {@code 400 (Bad Request)} if the publicationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the publicationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PublicationDTO> updatePublication(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PublicationDTO publicationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Publication : {}, {}", id, publicationDTO);
        if (publicationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publicationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publicationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        publicationDTO = publicationService.update(publicationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publicationDTO.getId().toString()))
            .body(publicationDTO);
    }

    /**
     * {@code PATCH  /publications/:id} : Partial updates given fields of an existing publication, field will ignore if it is null
     *
     * @param id the id of the publicationDTO to save.
     * @param publicationDTO the publicationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publicationDTO,
     * or with status {@code 400 (Bad Request)} if the publicationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the publicationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the publicationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PublicationDTO> partialUpdatePublication(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PublicationDTO publicationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Publication partially : {}, {}", id, publicationDTO);
        if (publicationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publicationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publicationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PublicationDTO> result = publicationService.partialUpdate(publicationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publicationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /publications} : get all the publications.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of publications in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PublicationDTO>> getAllPublications(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of Publications");

        Page<PublicationDTO> page = publicationService.listerPublications(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );

        return ResponseEntity.ok()
            .headers(headers)
            .body(page.getContent());
    }


    /**
     * {@code GET  /publications/:id} : get the "id" publication.
     *
     * @param id the id of the publicationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the publicationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublicationDTO> getPublication(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Publication : {}", id);
        Optional<PublicationDTO> publicationDTO = publicationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(publicationDTO);
    }

    /**
     * {@code DELETE  /publications/:id} : delete the "id" publication.
     *
     * @param id the id of the publicationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublication(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Publication : {}", id);
        publicationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /publications/_search?query=:query} : search for the publication corresponding
     * to the query.
     *
     * @param query the query of the publication search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PublicationDTO>> searchPublications(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Publications for query {}", query);
        try {
            Page<PublicationDTO> page = publicationService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
