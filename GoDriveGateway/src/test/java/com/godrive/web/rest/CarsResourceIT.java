package com.godrive.web.rest;

import static com.godrive.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.godrive.IntegrationTest;
import com.godrive.domain.Cars;
import com.godrive.repository.CarsRepository;
import com.godrive.service.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CarsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CarsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CAR_PLATE = "AAAAAAAAAA";
    private static final String UPDATED_CAR_PLATE = "BBBBBBBBBB";

    private static final String DEFAULT_VIN = "AAAAAAAAAA";
    private static final String UPDATED_VIN = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/cars";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CarsRepository carsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Cars cars;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cars createEntity(EntityManager em) {
        Cars cars = new Cars()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .carPlate(DEFAULT_CAR_PLATE)
            .vin(DEFAULT_VIN)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return cars;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cars createUpdatedEntity(EntityManager em) {
        Cars cars = new Cars()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .carPlate(UPDATED_CAR_PLATE)
            .vin(UPDATED_VIN)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return cars;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Cars.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        cars = createEntity(em);
    }

    @Test
    void createCars() throws Exception {
        int databaseSizeBeforeCreate = carsRepository.findAll().collectList().block().size();
        // Create the Cars
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cars))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeCreate + 1);
        Cars testCars = carsList.get(carsList.size() - 1);
        assertThat(testCars.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCars.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCars.getCarPlate()).isEqualTo(DEFAULT_CAR_PLATE);
        assertThat(testCars.getVin()).isEqualTo(DEFAULT_VIN);
        assertThat(testCars.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCars.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    void createCarsWithExistingId() throws Exception {
        // Create the Cars with an existing ID
        cars.setId(1L);

        int databaseSizeBeforeCreate = carsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cars))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCarsAsStream() {
        // Initialize the database
        carsRepository.save(cars).block();

        List<Cars> carsList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Cars.class)
            .getResponseBody()
            .filter(cars::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(carsList).isNotNull();
        assertThat(carsList).hasSize(1);
        Cars testCars = carsList.get(0);
        assertThat(testCars.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCars.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCars.getCarPlate()).isEqualTo(DEFAULT_CAR_PLATE);
        assertThat(testCars.getVin()).isEqualTo(DEFAULT_VIN);
        assertThat(testCars.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCars.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    void getAllCars() {
        // Initialize the database
        carsRepository.save(cars).block();

        // Get all the carsList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(cars.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].carPlate")
            .value(hasItem(DEFAULT_CAR_PLATE))
            .jsonPath("$.[*].vin")
            .value(hasItem(DEFAULT_VIN))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(sameInstant(DEFAULT_CREATED_AT)))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    void getCars() {
        // Initialize the database
        carsRepository.save(cars).block();

        // Get the cars
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cars.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cars.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.carPlate")
            .value(is(DEFAULT_CAR_PLATE))
            .jsonPath("$.vin")
            .value(is(DEFAULT_VIN))
            .jsonPath("$.createdAt")
            .value(is(sameInstant(DEFAULT_CREATED_AT)))
            .jsonPath("$.updatedAt")
            .value(is(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    void getNonExistingCars() {
        // Get the cars
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCars() throws Exception {
        // Initialize the database
        carsRepository.save(cars).block();

        int databaseSizeBeforeUpdate = carsRepository.findAll().collectList().block().size();

        // Update the cars
        Cars updatedCars = carsRepository.findById(cars.getId()).block();
        updatedCars
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .carPlate(UPDATED_CAR_PLATE)
            .vin(UPDATED_VIN)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCars.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCars))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
        Cars testCars = carsList.get(carsList.size() - 1);
        assertThat(testCars.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCars.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCars.getCarPlate()).isEqualTo(UPDATED_CAR_PLATE);
        assertThat(testCars.getVin()).isEqualTo(UPDATED_VIN);
        assertThat(testCars.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCars.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    void putNonExistingCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().collectList().block().size();
        cars.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cars.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cars))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().collectList().block().size();
        cars.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cars))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().collectList().block().size();
        cars.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cars))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCarsWithPatch() throws Exception {
        // Initialize the database
        carsRepository.save(cars).block();

        int databaseSizeBeforeUpdate = carsRepository.findAll().collectList().block().size();

        // Update the cars using partial update
        Cars partialUpdatedCars = new Cars();
        partialUpdatedCars.setId(cars.getId());

        partialUpdatedCars
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .carPlate(UPDATED_CAR_PLATE)
            .vin(UPDATED_VIN)
            .createdAt(UPDATED_CREATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCars.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCars))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
        Cars testCars = carsList.get(carsList.size() - 1);
        assertThat(testCars.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCars.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCars.getCarPlate()).isEqualTo(UPDATED_CAR_PLATE);
        assertThat(testCars.getVin()).isEqualTo(UPDATED_VIN);
        assertThat(testCars.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCars.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    void fullUpdateCarsWithPatch() throws Exception {
        // Initialize the database
        carsRepository.save(cars).block();

        int databaseSizeBeforeUpdate = carsRepository.findAll().collectList().block().size();

        // Update the cars using partial update
        Cars partialUpdatedCars = new Cars();
        partialUpdatedCars.setId(cars.getId());

        partialUpdatedCars
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .carPlate(UPDATED_CAR_PLATE)
            .vin(UPDATED_VIN)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCars.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCars))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
        Cars testCars = carsList.get(carsList.size() - 1);
        assertThat(testCars.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCars.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCars.getCarPlate()).isEqualTo(UPDATED_CAR_PLATE);
        assertThat(testCars.getVin()).isEqualTo(UPDATED_VIN);
        assertThat(testCars.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCars.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    void patchNonExistingCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().collectList().block().size();
        cars.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cars.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cars))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().collectList().block().size();
        cars.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cars))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().collectList().block().size();
        cars.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cars))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCars() {
        // Initialize the database
        carsRepository.save(cars).block();

        int databaseSizeBeforeDelete = carsRepository.findAll().collectList().block().size();

        // Delete the cars
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cars.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Cars> carsList = carsRepository.findAll().collectList().block();
        assertThat(carsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
