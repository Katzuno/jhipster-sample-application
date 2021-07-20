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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.godrive.domain.Codes}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CodesResource {

    private final Logger log = LoggerFactory.getLogger(CodesResource.class);

    private static final String ENTITY_NAME = "codes";

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
    public Mono<ResponseEntity<Codes>> createCodes(@RequestBody Codes codes) throws URISyntaxException {
        log.debug("REST request to save Codes : {}", codes);
        if (codes.getId() != null) {
            throw new BadRequestAlertException("A new codes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return codesRepository
            .save(codes)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/codes/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
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
    public Mono<ResponseEntity<Codes>> updateCodes(@PathVariable(value = "id", required = false) final Long id, @RequestBody Codes codes)
        throws URISyntaxException {
        log.debug("REST request to update Codes : {}, {}", id, codes);
        if (codes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, codes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return codesRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return codesRepository
                        .save(codes)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
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
    public Mono<ResponseEntity<Codes>> partialUpdateCodes(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Codes codes
    ) throws URISyntaxException {
        log.debug("REST request to partial update Codes partially : {}, {}", id, codes);
        if (codes.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, codes.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return codesRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Codes> result = codesRepository
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
                        .flatMap(codesRepository::save);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /codes} : get all the codes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of codes in body.
     */
    @GetMapping("/codes")
    public Mono<List<Codes>> getAllCodes() {
        log.debug("REST request to get all Codes");
        return codesRepository.findAll().collectList();
    }

    /**
     * {@code GET  /codes} : get all the codes as a stream.
     * @return the {@link Flux} of codes.
     */
    @GetMapping(value = "/codes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Codes> getAllCodesAsStream() {
        log.debug("REST request to get all Codes as a stream");
        return codesRepository.findAll();
    }

    /**
     * {@code GET  /codes/:id} : get the "id" codes.
     *
     * @param id the id of the codes to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the codes, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/codes/{id}")
    public Mono<ResponseEntity<Codes>> getCodes(@PathVariable Long id) {
        log.debug("REST request to get Codes : {}", id);
        Mono<Codes> codes = codesRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(codes);
    }

    /**
     * {@code DELETE  /codes/:id} : delete the "id" codes.
     *
     * @param id the id of the codes to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/codes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCodes(@PathVariable Long id) {
        log.debug("REST request to delete Codes : {}", id);
        return codesRepository
            .deleteById(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
