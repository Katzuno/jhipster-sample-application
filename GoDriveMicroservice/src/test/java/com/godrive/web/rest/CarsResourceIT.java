package com.godrive.web.rest;

import static com.godrive.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.godrive.IntegrationTest;
import com.godrive.domain.Cars;
import com.godrive.repository.CarsRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CarsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restCarsMockMvc;

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

    @BeforeEach
    public void initTest() {
        cars = createEntity(em);
    }

    @Test
    @Transactional
    void createCars() throws Exception {
        int databaseSizeBeforeCreate = carsRepository.findAll().size();
        // Create the Cars
        restCarsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cars)))
            .andExpect(status().isCreated());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
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
    @Transactional
    void createCarsWithExistingId() throws Exception {
        // Create the Cars with an existing ID
        cars.setId(1L);

        int databaseSizeBeforeCreate = carsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cars)))
            .andExpect(status().isBadRequest());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
        assertThat(carsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCars() throws Exception {
        // Initialize the database
        carsRepository.saveAndFlush(cars);

        // Get all the carsList
        restCarsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cars.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].carPlate").value(hasItem(DEFAULT_CAR_PLATE)))
            .andExpect(jsonPath("$.[*].vin").value(hasItem(DEFAULT_VIN)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    @Transactional
    void getCars() throws Exception {
        // Initialize the database
        carsRepository.saveAndFlush(cars);

        // Get the cars
        restCarsMockMvc
            .perform(get(ENTITY_API_URL_ID, cars.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cars.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.carPlate").value(DEFAULT_CAR_PLATE))
            .andExpect(jsonPath("$.vin").value(DEFAULT_VIN))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingCars() throws Exception {
        // Get the cars
        restCarsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCars() throws Exception {
        // Initialize the database
        carsRepository.saveAndFlush(cars);

        int databaseSizeBeforeUpdate = carsRepository.findAll().size();

        // Update the cars
        Cars updatedCars = carsRepository.findById(cars.getId()).get();
        // Disconnect from session so that the updates on updatedCars are not directly saved in db
        em.detach(updatedCars);
        updatedCars
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .carPlate(UPDATED_CAR_PLATE)
            .vin(UPDATED_VIN)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCarsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCars.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCars))
            )
            .andExpect(status().isOk());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
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
    @Transactional
    void putNonExistingCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().size();
        cars.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cars.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cars))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().size();
        cars.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cars))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().size();
        cars.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(cars)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCarsWithPatch() throws Exception {
        // Initialize the database
        carsRepository.saveAndFlush(cars);

        int databaseSizeBeforeUpdate = carsRepository.findAll().size();

        // Update the cars using partial update
        Cars partialUpdatedCars = new Cars();
        partialUpdatedCars.setId(cars.getId());

        partialUpdatedCars
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .carPlate(UPDATED_CAR_PLATE)
            .vin(UPDATED_VIN)
            .createdAt(UPDATED_CREATED_AT);

        restCarsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCars.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCars))
            )
            .andExpect(status().isOk());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
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
    @Transactional
    void fullUpdateCarsWithPatch() throws Exception {
        // Initialize the database
        carsRepository.saveAndFlush(cars);

        int databaseSizeBeforeUpdate = carsRepository.findAll().size();

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

        restCarsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCars.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCars))
            )
            .andExpect(status().isOk());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
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
    @Transactional
    void patchNonExistingCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().size();
        cars.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cars.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cars))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().size();
        cars.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cars))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCars() throws Exception {
        int databaseSizeBeforeUpdate = carsRepository.findAll().size();
        cars.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(cars)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cars in the database
        List<Cars> carsList = carsRepository.findAll();
        assertThat(carsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCars() throws Exception {
        // Initialize the database
        carsRepository.saveAndFlush(cars);

        int databaseSizeBeforeDelete = carsRepository.findAll().size();

        // Delete the cars
        restCarsMockMvc
            .perform(delete(ENTITY_API_URL_ID, cars.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Cars> carsList = carsRepository.findAll();
        assertThat(carsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
