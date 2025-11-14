package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.repository.ConseillerRepository;
import com.nextstepsenegal.app.service.ConseillerService;
import com.nextstepsenegal.app.service.dto.ConseillerDTO;
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
 * REST controller for managing {@link com.nextstepsenegal.app.domain.Conseiller}.
 */
@RestController
@RequestMapping("/api/conseillers")
public class ConseillerResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConseillerResource.class);

    private static final String ENTITY_NAME = "userManagementServiceConseiller";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConseillerService conseillerService;

    private final ConseillerRepository conseillerRepository;

    public ConseillerResource(ConseillerService conseillerService, ConseillerRepository conseillerRepository) {
        this.conseillerService = conseillerService;
        this.conseillerRepository = conseillerRepository;
    }

    /**
     * {@code POST  /conseillers} : Create a new conseiller.
     *
     * @param conseillerDTO the conseillerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new conseillerDTO, or with status {@code 400 (Bad Request)} if the conseiller has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ConseillerDTO> createConseiller(@Valid @RequestBody ConseillerDTO conseillerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Conseiller : {}", conseillerDTO);
        if (conseillerDTO.getId() != null) {
            throw new BadRequestAlertException("A new conseiller cannot already have an ID", ENTITY_NAME, "idexists");
        }
        conseillerDTO = conseillerService.save(conseillerDTO);
        return ResponseEntity.created(new URI("/api/conseillers/" + conseillerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, conseillerDTO.getId().toString()))
            .body(conseillerDTO);
    }

    /**
     * {@code PUT  /conseillers/:id} : Updates an existing conseiller.
     *
     * @param id the id of the conseillerDTO to save.
     * @param conseillerDTO the conseillerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conseillerDTO,
     * or with status {@code 400 (Bad Request)} if the conseillerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conseillerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConseillerDTO> updateConseiller(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ConseillerDTO conseillerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Conseiller : {}, {}", id, conseillerDTO);
        if (conseillerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conseillerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!conseillerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        conseillerDTO = conseillerService.update(conseillerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, conseillerDTO.getId().toString()))
            .body(conseillerDTO);
    }

    /**
     * {@code PATCH  /conseillers/:id} : Partial updates given fields of an existing conseiller, field will ignore if it is null
     *
     * @param id the id of the conseillerDTO to save.
     * @param conseillerDTO the conseillerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conseillerDTO,
     * or with status {@code 400 (Bad Request)} if the conseillerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the conseillerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the conseillerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ConseillerDTO> partialUpdateConseiller(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ConseillerDTO conseillerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Conseiller partially : {}, {}", id, conseillerDTO);
        if (conseillerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conseillerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!conseillerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ConseillerDTO> result = conseillerService.partialUpdate(conseillerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, conseillerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /conseillers} : get all the conseillers.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of conseillers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ConseillerDTO>> getAllConseillers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Conseillers");
        Page<ConseillerDTO> page;
        if (eagerload) {
            page = conseillerService.findAllWithEagerRelationships(pageable);
        } else {
            page = conseillerService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /conseillers/:id} : get the "id" conseiller.
     *
     * @param id the id of the conseillerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the conseillerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConseillerDTO> getConseiller(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Conseiller : {}", id);
        Optional<ConseillerDTO> conseillerDTO = conseillerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(conseillerDTO);
    }

    /**
     * {@code DELETE  /conseillers/:id} : delete the "id" conseiller.
     *
     * @param id the id of the conseillerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConseiller(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Conseiller : {}", id);
        conseillerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /conseillers/_search?query=:query} : search for the conseiller corresponding
     * to the query.
     *
     * @param query the query of the conseiller search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ConseillerDTO>> searchConseillers(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Conseillers for query {}", query);
        try {
            Page<ConseillerDTO> page = conseillerService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
