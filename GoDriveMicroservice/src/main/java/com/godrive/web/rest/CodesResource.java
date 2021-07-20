package com.godrive.web.rest;

import com.godrive.domain.Codes;
import com.godrive.repository.CodesRepository;
import com.godrive.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.godrive.domain.Codes}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CodesResource {

    private final Logger log = LoggerFactory.getLogger(CodesResource.class);

    private static final String ENTITY_NAME = "goDriveMicroserviceCodes";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CodesRepository codesRepository;

    public CodesResource(CodesRepository codesRepository) {
        this.codesRepository = codesRepository;
    }

    /**
     * {@code POST  /codes} : Create a new codes.
     *
     * @param codes the codes to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new codes, or with status {@code 400 (Bad Request)} if the codes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/codes")
    public ResponseEntity<Codes> createCodes(@RequestBody Codes codes) throws URISyntaxException {
        log.debug("REST request to save Codes : {}", codes);
        if (codes.getId() != null) {
            throw new BadRequestAlertException("A new codes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Codes result = codesRepository.save(codes);
        return ResponseEntity
            .created(new URI("/api/codes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /codes/:id} : Updates an existing codes.
     *
     * @param id the id of the codes to save.
     * @param codes the codes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated codes,
     * or with status {@code 400 (Bad Request)} if the codes is not valid,
     * or with status {@code 500 (Internal Server Error)} if the codes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/codes/{id}")
    public ResponseEntity<Codes> updateCodes(@PathVariable(value = "id", required = false) final Long id, @RequestBody Codes codes)
        throws URISyntaxException {
        log.debug("REST request to update Codes : {}, {}", id, codes);
        if (codes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, codes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!codesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Codes result = codesRepository.save(codes);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, codes.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /codes/:id} : Partial updates given fields of an existing codes, field will ignore if it is null
     *
     * @param id the id of the codes to save.
     * @param codes the codes to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated codes,
     * or with status {@code 400 (Bad Request)} if the codes is not valid,
     * or with status {@code 404 (Not Found)} if the codes is not found,
     * or with status {@code 500 (Internal Server Error)} if the codes couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/codes/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Codes> partialUpdateCodes(@PathVariable(value = "id", required = false) final Long id, @RequestBody Codes codes)
        throws URISyntaxException {
        log.debug("REST request to partial update Codes partially : {}, {}", id, codes);
        if (codes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, codes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!codesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Codes> result = codesRepository
            .findById(codes.getId())
            .map(
                existingCodes -> {
                    if (codes.getMode() != null) {
                        existingCodes.setMode(codes.getMode());
                    }
                    if (codes.getSegment() != null) {
                        existingCodes.setSegment(codes.getSegment());
                    }
                    if (codes.getCode() != null) {
                        existingCodes.setCode(codes.getCode());
                    }
                    if (codes.getDimension() != null) {
                        existingCodes.setDimension(codes.getDimension());
                    }
                    if (codes.getDescription() != null) {
                        existingCodes.setDescription(codes.getDescription());
                    }
                    if (codes.getMinVal() != null) {
                        existingCodes.setMinVal(codes.getMinVal());
                    }
                    if (codes.getMaxVal() != null) {
                        existingCodes.setMaxVal(codes.getMaxVal());
                    }
                    if (codes.getUnits() != null) {
                        existingCodes.setUnits(codes.getUnits());
                    }
                    if (codes.getEnabled() != null) {
                        existingCodes.setEnabled(codes.getEnabled());
                    }
                    if (codes.getCreatedAt() != null) {
                        existingCodes.setCreatedAt(codes.getCreatedAt());
                    }
                    if (codes.getUpdatedAt() != null) {
                        existingCodes.setUpdatedAt(codes.getUpdatedAt());
                    }

                    return existingCodes;
                }
            )
            .map(codesRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, codes.getId().toString())
        );
    }

    /**
     * {@code GET  /codes} : get all the codes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of codes in body.
     */
    @GetMapping("/codes")
    public List<Codes> getAllCodes() {
        log.debug("REST request to get all Codes");
        return codesRepository.findAll();
    }

    /**
     * {@code GET  /codes/:id} : get the "id" codes.
     *
     * @param id the id of the codes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the codes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/codes/{id}")
    public ResponseEntity<Codes> getCodes(@PathVariable Long id) {
        log.debug("REST request to get Codes : {}", id);
        Optional<Codes> codes = codesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(codes);
    }

    /**
     * {@code DELETE  /codes/:id} : delete the "id" codes.
     *
     * @param id the id of the codes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/codes/{id}")
    public ResponseEntity<Void> deleteCodes(@PathVariable Long id) {
        log.debug("REST request to delete Codes : {}", id);
        codesRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
