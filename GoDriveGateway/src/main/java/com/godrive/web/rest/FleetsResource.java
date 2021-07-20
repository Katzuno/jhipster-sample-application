package com.godrive.web.rest;

import com.godrive.domain.Fleets;
import com.godrive.repository.FleetsRepository;
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
 * REST controller for managing {@link com.godrive.domain.Fleets}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class FleetsResource {

    private final Logger log = LoggerFactory.getLogger(FleetsResource.class);

    private static final String ENTITY_NAME = "fleets";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FleetsRepository fleetsRepository;

    public FleetsResource(FleetsRepository fleetsRepository) {
        this.fleetsRepository = fleetsRepository;
    }

    /**
     * {@code POST  /fleets} : Create a new fleets.
     *
     * @param fleets the fleets to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fleets, or with status {@code 400 (Bad Request)} if the fleets has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/fleets")
    public Mono<ResponseEntity<Fleets>> createFleets(@RequestBody Fleets fleets) throws URISyntaxException {
        log.debug("REST request to save Fleets : {}", fleets);
        if (fleets.getId() != null) {
            throw new BadRequestAlertException("A new fleets cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return fleetsRepository
            .save(fleets)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/fleets/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /fleets/:id} : Updates an existing fleets.
     *
     * @param id the id of the fleets to save.
     * @param fleets the fleets to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fleets,
     * or with status {@code 400 (Bad Request)} if the fleets is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fleets couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/fleets/{id}")
    public Mono<ResponseEntity<Fleets>> updateFleets(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Fleets fleets
    ) throws URISyntaxException {
        log.debug("REST request to update Fleets : {}, {}", id, fleets);
        if (fleets.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fleets.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return fleetsRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return fleetsRepository
                        .save(fleets)
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
     * {@code PATCH  /fleets/:id} : Partial updates given fields of an existing fleets, field will ignore if it is null
     *
     * @param id the id of the fleets to save.
     * @param fleets the fleets to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fleets,
     * or with status {@code 400 (Bad Request)} if the fleets is not valid,
     * or with status {@code 404 (Not Found)} if the fleets is not found,
     * or with status {@code 500 (Internal Server Error)} if the fleets couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/fleets/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Fleets>> partialUpdateFleets(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Fleets fleets
    ) throws URISyntaxException {
        log.debug("REST request to partial update Fleets partially : {}, {}", id, fleets);
        if (fleets.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fleets.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return fleetsRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Fleets> result = fleetsRepository
                        .findById(fleets.getId())
                        .map(
                            existingFleets -> {
                                if (fleets.getName() != null) {
                                    existingFleets.setName(fleets.getName());
                                }
                                if (fleets.getDescription() != null) {
                                    existingFleets.setDescription(fleets.getDescription());
                                }
                                if (fleets.getCreatedAt() != null) {
                                    existingFleets.setCreatedAt(fleets.getCreatedAt());
                                }
                                if (fleets.getUpdatedAt() != null) {
                                    existingFleets.setUpdatedAt(fleets.getUpdatedAt());
                                }

                                return existingFleets;
                            }
                        )
                        .flatMap(fleetsRepository::save);

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
     * {@code GET  /fleets} : get all the fleets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fleets in body.
     */
    @GetMapping("/fleets")
    public Mono<List<Fleets>> getAllFleets() {
        log.debug("REST request to get all Fleets");
        return fleetsRepository.findAll().collectList();
    }

    /**
     * {@code GET  /fleets} : get all the fleets as a stream.
     * @return the {@link Flux} of fleets.
     */
    @GetMapping(value = "/fleets", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Fleets> getAllFleetsAsStream() {
        log.debug("REST request to get all Fleets as a stream");
        return fleetsRepository.findAll();
    }

    /**
     * {@code GET  /fleets/:id} : get the "id" fleets.
     *
     * @param id the id of the fleets to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fleets, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/fleets/{id}")
    public Mono<ResponseEntity<Fleets>> getFleets(@PathVariable Long id) {
        log.debug("REST request to get Fleets : {}", id);
        Mono<Fleets> fleets = fleetsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(fleets);
    }

    /**
     * {@code DELETE  /fleets/:id} : delete the "id" fleets.
     *
     * @param id the id of the fleets to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/fleets/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteFleets(@PathVariable Long id) {
        log.debug("REST request to delete Fleets : {}", id);
        return fleetsRepository
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
