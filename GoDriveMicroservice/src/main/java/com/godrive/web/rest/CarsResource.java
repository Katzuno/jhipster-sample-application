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
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.godrive.domain.Cars}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CarsResource {

    private final Logger log = LoggerFactory.getLogger(CarsResource.class);

    private static final String ENTITY_NAME = "goDriveMicroserviceCars";

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
    public ResponseEntity<Cars> createCars(@RequestBody Cars cars) throws URISyntaxException {
        log.debug("REST request to save Cars : {}", cars);
        if (cars.getId() != null) {
            throw new BadRequestAlertException("A new cars cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Cars result = carsRepository.save(cars);
        return ResponseEntity
            .created(new URI("/api/cars/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
    public ResponseEntity<Cars> updateCars(@PathVariable(value = "id", required = false) final Long id, @RequestBody Cars cars)
        throws URISyntaxException {
        log.debug("REST request to update Cars : {}, {}", id, cars);
        if (cars.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cars.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Cars result = carsRepository.save(cars);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cars.getId().toString()))
            .body(result);
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
    public ResponseEntity<Cars> partialUpdateCars(@PathVariable(value = "id", required = false) final Long id, @RequestBody Cars cars)
        throws URISyntaxException {
        log.debug("REST request to partial update Cars partially : {}, {}", id, cars);
        if (cars.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cars.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Cars> result = carsRepository
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
            .map(carsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cars.getId().toString())
        );
    }

    /**
     * {@code GET  /cars} : get all the cars.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cars in body.
     */
    @GetMapping("/cars")
    public List<Cars> getAllCars() {
        log.debug("REST request to get all Cars");
        return carsRepository.findAll();
    }

    /**
     * {@code GET  /cars/:id} : get the "id" cars.
     *
     * @param id the id of the cars to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cars, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cars/{id}")
    public ResponseEntity<Cars> getCars(@PathVariable Long id) {
        log.debug("REST request to get Cars : {}", id);
        Optional<Cars> cars = carsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(cars);
    }

    /**
     * {@code DELETE  /cars/:id} : delete the "id" cars.
     *
     * @param id the id of the cars to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCars(@PathVariable Long id) {
        log.debug("REST request to delete Cars : {}", id);
        carsRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
