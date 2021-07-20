package com.godrive.web.rest;

import static com.godrive.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.godrive.IntegrationTest;
import com.godrive.domain.Fleets;
import com.godrive.repository.FleetsRepository;
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
 * Integration tests for the {@link FleetsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FleetsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/fleets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FleetsRepository fleetsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFleetsMockMvc;

    private Fleets fleets;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fleets createEntity(EntityManager em) {
        Fleets fleets = new Fleets()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return fleets;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fleets createUpdatedEntity(EntityManager em) {
        Fleets fleets = new Fleets()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return fleets;
    }

    @BeforeEach
    public void initTest() {
        fleets = createEntity(em);
    }

    @Test
    @Transactional
    void createFleets() throws Exception {
        int databaseSizeBeforeCreate = fleetsRepository.findAll().size();
        // Create the Fleets
        restFleetsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fleets)))
            .andExpect(status().isCreated());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeCreate + 1);
        Fleets testFleets = fleetsList.get(fleetsList.size() - 1);
        assertThat(testFleets.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFleets.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFleets.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testFleets.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createFleetsWithExistingId() throws Exception {
        // Create the Fleets with an existing ID
        fleets.setId(1L);

        int databaseSizeBeforeCreate = fleetsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFleetsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fleets)))
            .andExpect(status().isBadRequest());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFleets() throws Exception {
        // Initialize the database
        fleetsRepository.saveAndFlush(fleets);

        // Get all the fleetsList
        restFleetsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fleets.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    @Transactional
    void getFleets() throws Exception {
        // Initialize the database
        fleetsRepository.saveAndFlush(fleets);

        // Get the fleets
        restFleetsMockMvc
            .perform(get(ENTITY_API_URL_ID, fleets.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fleets.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingFleets() throws Exception {
        // Get the fleets
        restFleetsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFleets() throws Exception {
        // Initialize the database
        fleetsRepository.saveAndFlush(fleets);

        int databaseSizeBeforeUpdate = fleetsRepository.findAll().size();

        // Update the fleets
        Fleets updatedFleets = fleetsRepository.findById(fleets.getId()).get();
        // Disconnect from session so that the updates on updatedFleets are not directly saved in db
        em.detach(updatedFleets);
        updatedFleets.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restFleetsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFleets.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFleets))
            )
            .andExpect(status().isOk());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
        Fleets testFleets = fleetsList.get(fleetsList.size() - 1);
        assertThat(testFleets.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFleets.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFleets.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testFleets.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().size();
        fleets.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFleetsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fleets.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fleets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().size();
        fleets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFleetsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(fleets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().size();
        fleets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFleetsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(fleets)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFleetsWithPatch() throws Exception {
        // Initialize the database
        fleetsRepository.saveAndFlush(fleets);

        int databaseSizeBeforeUpdate = fleetsRepository.findAll().size();

        // Update the fleets using partial update
        Fleets partialUpdatedFleets = new Fleets();
        partialUpdatedFleets.setId(fleets.getId());

        partialUpdatedFleets.createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restFleetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFleets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFleets))
            )
            .andExpect(status().isOk());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
        Fleets testFleets = fleetsList.get(fleetsList.size() - 1);
        assertThat(testFleets.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFleets.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFleets.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testFleets.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateFleetsWithPatch() throws Exception {
        // Initialize the database
        fleetsRepository.saveAndFlush(fleets);

        int databaseSizeBeforeUpdate = fleetsRepository.findAll().size();

        // Update the fleets using partial update
        Fleets partialUpdatedFleets = new Fleets();
        partialUpdatedFleets.setId(fleets.getId());

        partialUpdatedFleets
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restFleetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFleets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFleets))
            )
            .andExpect(status().isOk());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
        Fleets testFleets = fleetsList.get(fleetsList.size() - 1);
        assertThat(testFleets.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFleets.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFleets.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testFleets.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().size();
        fleets.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFleetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fleets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fleets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().size();
        fleets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFleetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(fleets))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().size();
        fleets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFleetsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(fleets)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFleets() throws Exception {
        // Initialize the database
        fleetsRepository.saveAndFlush(fleets);

        int databaseSizeBeforeDelete = fleetsRepository.findAll().size();

        // Delete the fleets
        restFleetsMockMvc
            .perform(delete(ENTITY_API_URL_ID, fleets.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Fleets> fleetsList = fleetsRepository.findAll();
        assertThat(fleetsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
