package com.nextstepsenegal.app.web.rest;

import com.nextstepsenegal.app.repository.EleveRepository;
import com.nextstepsenegal.app.service.EleveService;
import com.nextstepsenegal.app.service.dto.EleveDTO;
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
 * REST controller for managing {@link com.nextstepsenegal.app.domain.Eleve}.
 */
@RestController
@RequestMapping("/api/eleves")
public class EleveResource {

    private static final Logger LOG = LoggerFactory.getLogger(EleveResource.class);

    private static final String ENTITY_NAME = "userManagementServiceEleve";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EleveService eleveService;

    private final EleveRepository eleveRepository;

    public EleveResource(EleveService eleveService, EleveRepository eleveRepository) {
        this.eleveService = eleveService;
        this.eleveRepository = eleveRepository;
    }

    /**
     * {@code POST  /eleves} : Create a new eleve.
     *
     * @param eleveDTO the eleveDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eleveDTO, or with status {@code 400 (Bad Request)} if the eleve has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EleveDTO> createEleve(@Valid @RequestBody EleveDTO eleveDTO) throws URISyntaxException {
        LOG.debug("REST request to save Eleve : {}", eleveDTO);
        if (eleveDTO.getId() != null) {
            throw new BadRequestAlertException("A new eleve cannot already have an ID", ENTITY_NAME, "idexists");
        }
        eleveDTO = eleveService.save(eleveDTO);
        return ResponseEntity.created(new URI("/api/eleves/" + eleveDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, eleveDTO.getId().toString()))
            .body(eleveDTO);
    }

    /**
     * {@code PUT  /eleves/:id} : Updates an existing eleve.
     *
     * @param id the id of the eleveDTO to save.
     * @param eleveDTO the eleveDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eleveDTO,
     * or with status {@code 400 (Bad Request)} if the eleveDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eleveDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EleveDTO> updateEleve(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EleveDTO eleveDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Eleve : {}, {}", id, eleveDTO);
        if (eleveDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eleveDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eleveRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        eleveDTO = eleveService.update(eleveDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eleveDTO.getId().toString()))
            .body(eleveDTO);
    }

    /**
     * {@code PATCH  /eleves/:id} : Partial updates given fields of an existing eleve, field will ignore if it is null
     *
     * @param id the id of the eleveDTO to save.
     * @param eleveDTO the eleveDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eleveDTO,
     * or with status {@code 400 (Bad Request)} if the eleveDTO is not valid,
     * or with status {@code 404 (Not Found)} if the eleveDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the eleveDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EleveDTO> partialUpdateEleve(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EleveDTO eleveDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Eleve partially : {}, {}", id, eleveDTO);
        if (eleveDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eleveDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eleveRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EleveDTO> result = eleveService.partialUpdate(eleveDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, eleveDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /eleves} : get all the eleves.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eleves in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EleveDTO>> getAllEleves(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of Eleves");
        Page<EleveDTO> page;
        if (eagerload) {
            page = eleveService.findAllWithEagerRelationships(pageable);
        } else {
            page = eleveService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /eleves/:id} : get the "id" eleve.
     *
     * @param id the id of the eleveDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eleveDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EleveDTO> getEleve(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Eleve : {}", id);
        Optional<EleveDTO> eleveDTO = eleveService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eleveDTO);
    }

    /**
     * {@code DELETE  /eleves/:id} : delete the "id" eleve.
     *
     * @param id the id of the eleveDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEleve(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Eleve : {}", id);
        eleveService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /eleves/_search?query=:query} : search for the eleve corresponding
     * to the query.
     *
     * @param query the query of the eleve search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<EleveDTO>> searchEleves(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Eleves for query {}", query);
        try {
            Page<EleveDTO> page = eleveService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
