package com.godrive.web.rest;

import com.godrive.domain.Cars;
import com.godrive.repository.CarsRepository;
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
 * REST controller for managing {@link com.godrive.domain.Cars}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CarsResource {

    private final Logger log = LoggerFactory.getLogger(CarsResource.class);

    private static final String ENTITY_NAME = "cars";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarsRepository carsRepository;

    public CarsResource(CarsRepository carsRepository) {
        this.carsRepository = carsRepository;
    }

    /**
     * {@code POST  /cars} : Create a new cars.
     *
     * @param cars the cars to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cars, or with status {@code 400 (Bad Request)} if the cars has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cars")
    public Mono<ResponseEntity<Cars>> createCars(@RequestBody Cars cars) throws URISyntaxException {
        log.debug("REST request to save Cars : {}", cars);
        if (cars.getId() != null) {
            throw new BadRequestAlertException("A new cars cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return carsRepository
            .save(cars)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/cars/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /cars/:id} : Updates an existing cars.
     *
     * @param id the id of the cars to save.
     * @param cars the cars to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cars,
     * or with status {@code 400 (Bad Request)} if the cars is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cars couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cars/{id}")
    public Mono<ResponseEntity<Cars>> updateCars(@PathVariable(value = "id", required = false) final Long id, @RequestBody Cars cars)
        throws URISyntaxException {
        log.debug("REST request to update Cars : {}, {}", id, cars);
        if (cars.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cars.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return carsRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return carsRepository
                        .save(cars)
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
     * {@code PATCH  /cars/:id} : Partial updates given fields of an existing cars, field will ignore if it is null
     *
     * @param id the id of the cars to save.
     * @param cars the cars to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cars,
     * or with status {@code 400 (Bad Request)} if the cars is not valid,
     * or with status {@code 404 (Not Found)} if the cars is not found,
     * or with status {@code 500 (Internal Server Error)} if the cars couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cars/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Cars>> partialUpdateCars(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Cars cars
    ) throws URISyntaxException {
        log.debug("REST request to partial update Cars partially : {}, {}", id, cars);
        if (cars.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cars.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return carsRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Cars> result = carsRepository
                        .findById(cars.getId())
                        .map(
                            existingCars -> {
                                if (cars.getName() != null) {
                                    existingCars.setName(cars.getName());
                                }
                                if (cars.getDescription() != null) {
                                    existingCars.setDescription(cars.getDescription());
                                }
                                if (cars.getCarPlate() != null) {
                                    existingCars.setCarPlate(cars.getCarPlate());
                                }
                                if (cars.getVin() != null) {
                                    existingCars.setVin(cars.getVin());
                                }
                                if (cars.getCreatedAt() != null) {
                                    existingCars.setCreatedAt(cars.getCreatedAt());
                                }
                                if (cars.getUpdatedAt() != null) {
                                    existingCars.setUpdatedAt(cars.getUpdatedAt());
                                }

                                return existingCars;
                            }
                        )
                        .flatMap(carsRepository::save);

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
     * {@code GET  /cars} : get all the cars.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cars in body.
     */
    @GetMapping("/cars")
    public Mono<List<Cars>> getAllCars() {
        log.debug("REST request to get all Cars");
        return carsRepository.findAll().collectList();
    }

    /**
     * {@code GET  /cars} : get all the cars as a stream.
     * @return the {@link Flux} of cars.
     */
    @GetMapping(value = "/cars", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Cars> getAllCarsAsStream() {
        log.debug("REST request to get all Cars as a stream");
        return carsRepository.findAll();
    }

    /**
     * {@code GET  /cars/:id} : get the "id" cars.
     *
     * @param id the id of the cars to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cars, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cars/{id}")
    public Mono<ResponseEntity<Cars>> getCars(@PathVariable Long id) {
        log.debug("REST request to get Cars : {}", id);
        Mono<Cars> cars = carsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cars);
    }

    /**
     * {@code DELETE  /cars/:id} : delete the "id" cars.
     *
     * @param id the id of the cars to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cars/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCars(@PathVariable Long id) {
        log.debug("REST request to delete Cars : {}", id);
        return carsRepository
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
