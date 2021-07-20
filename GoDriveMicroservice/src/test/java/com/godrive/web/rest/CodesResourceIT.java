package com.godrive.web.rest;

import static com.godrive.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.godrive.IntegrationTest;
import com.godrive.domain.Codes;
import com.godrive.repository.CodesRepository;
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
 * Integration tests for the {@link CodesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CodesResourceIT {

    private static final String DEFAULT_MODE = "AAAAAAAAAA";
    private static final String UPDATED_MODE = "BBBBBBBBBB";

    private static final String DEFAULT_SEGMENT = "AAAAAAAAAA";
    private static final String UPDATED_SEGMENT = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Float DEFAULT_DIMENSION = 1F;
    private static final Float UPDATED_DIMENSION = 2F;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_MIN_VAL = "AAAAAAAAAA";
    private static final String UPDATED_MIN_VAL = "BBBBBBBBBB";

    private static final String DEFAULT_MAX_VAL = "AAAAAAAAAA";
    private static final String UPDATED_MAX_VAL = "BBBBBBBBBB";

    private static final String DEFAULT_UNITS = "AAAAAAAAAA";
    private static final String UPDATED_UNITS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/codes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CodesRepository codesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCodesMockMvc;

    private Codes codes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Codes createEntity(EntityManager em) {
        Codes codes = new Codes()
            .mode(DEFAULT_MODE)
            .segment(DEFAULT_SEGMENT)
            .code(DEFAULT_CODE)
            .dimension(DEFAULT_DIMENSION)
            .description(DEFAULT_DESCRIPTION)
            .minVal(DEFAULT_MIN_VAL)
            .maxVal(DEFAULT_MAX_VAL)
            .units(DEFAULT_UNITS)
            .enabled(DEFAULT_ENABLED)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return codes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Codes createUpdatedEntity(EntityManager em) {
        Codes codes = new Codes()
            .mode(UPDATED_MODE)
            .segment(UPDATED_SEGMENT)
            .code(UPDATED_CODE)
            .dimension(UPDATED_DIMENSION)
            .description(UPDATED_DESCRIPTION)
            .minVal(UPDATED_MIN_VAL)
            .maxVal(UPDATED_MAX_VAL)
            .units(UPDATED_UNITS)
            .enabled(UPDATED_ENABLED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return codes;
    }

    @BeforeEach
    public void initTest() {
        codes = createEntity(em);
    }

    @Test
    @Transactional
    void createCodes() throws Exception {
        int databaseSizeBeforeCreate = codesRepository.findAll().size();
        // Create the Codes
        restCodesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(codes)))
            .andExpect(status().isCreated());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeCreate + 1);
        Codes testCodes = codesList.get(codesList.size() - 1);
        assertThat(testCodes.getMode()).isEqualTo(DEFAULT_MODE);
        assertThat(testCodes.getSegment()).isEqualTo(DEFAULT_SEGMENT);
        assertThat(testCodes.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testCodes.getDimension()).isEqualTo(DEFAULT_DIMENSION);
        assertThat(testCodes.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCodes.getMinVal()).isEqualTo(DEFAULT_MIN_VAL);
        assertThat(testCodes.getMaxVal()).isEqualTo(DEFAULT_MAX_VAL);
        assertThat(testCodes.getUnits()).isEqualTo(DEFAULT_UNITS);
        assertThat(testCodes.getEnabled()).isEqualTo(DEFAULT_ENABLED);
        assertThat(testCodes.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCodes.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createCodesWithExistingId() throws Exception {
        // Create the Codes with an existing ID
        codes.setId(1L);

        int databaseSizeBeforeCreate = codesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCodesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(codes)))
            .andExpect(status().isBadRequest());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCodes() throws Exception {
        // Initialize the database
        codesRepository.saveAndFlush(codes);

        // Get all the codesList
        restCodesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(codes.getId().intValue())))
            .andExpect(jsonPath("$.[*].mode").value(hasItem(DEFAULT_MODE)))
            .andExpect(jsonPath("$.[*].segment").value(hasItem(DEFAULT_SEGMENT)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].dimension").value(hasItem(DEFAULT_DIMENSION.doubleValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].minVal").value(hasItem(DEFAULT_MIN_VAL)))
            .andExpect(jsonPath("$.[*].maxVal").value(hasItem(DEFAULT_MAX_VAL)))
            .andExpect(jsonPath("$.[*].units").value(hasItem(DEFAULT_UNITS)))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(sameInstant(DEFAULT_UPDATED_AT))));
    }

    @Test
    @Transactional
    void getCodes() throws Exception {
        // Initialize the database
        codesRepository.saveAndFlush(codes);

        // Get the codes
        restCodesMockMvc
            .perform(get(ENTITY_API_URL_ID, codes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(codes.getId().intValue()))
            .andExpect(jsonPath("$.mode").value(DEFAULT_MODE))
            .andExpect(jsonPath("$.segment").value(DEFAULT_SEGMENT))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.dimension").value(DEFAULT_DIMENSION.doubleValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.minVal").value(DEFAULT_MIN_VAL))
            .andExpect(jsonPath("$.maxVal").value(DEFAULT_MAX_VAL))
            .andExpect(jsonPath("$.units").value(DEFAULT_UNITS))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)))
            .andExpect(jsonPath("$.updatedAt").value(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    @Transactional
    void getNonExistingCodes() throws Exception {
        // Get the codes
        restCodesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCodes() throws Exception {
        // Initialize the database
        codesRepository.saveAndFlush(codes);

        int databaseSizeBeforeUpdate = codesRepository.findAll().size();

        // Update the codes
        Codes updatedCodes = codesRepository.findById(codes.getId()).get();
        // Disconnect from session so that the updates on updatedCodes are not directly saved in db
        em.detach(updatedCodes);
        updatedCodes
            .mode(UPDATED_MODE)
            .segment(UPDATED_SEGMENT)
            .code(UPDATED_CODE)
            .dimension(UPDATED_DIMENSION)
            .description(UPDATED_DESCRIPTION)
            .minVal(UPDATED_MIN_VAL)
            .maxVal(UPDATED_MAX_VAL)
            .units(UPDATED_UNITS)
            .enabled(UPDATED_ENABLED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCodesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCodes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCodes))
            )
            .andExpect(status().isOk());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
        Codes testCodes = codesList.get(codesList.size() - 1);
        assertThat(testCodes.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testCodes.getSegment()).isEqualTo(UPDATED_SEGMENT);
        assertThat(testCodes.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCodes.getDimension()).isEqualTo(UPDATED_DIMENSION);
        assertThat(testCodes.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCodes.getMinVal()).isEqualTo(UPDATED_MIN_VAL);
        assertThat(testCodes.getMaxVal()).isEqualTo(UPDATED_MAX_VAL);
        assertThat(testCodes.getUnits()).isEqualTo(UPDATED_UNITS);
        assertThat(testCodes.getEnabled()).isEqualTo(UPDATED_ENABLED);
        assertThat(testCodes.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCodes.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().size();
        codes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCodesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, codes.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(codes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().size();
        codes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCodesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(codes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().size();
        codes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCodesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(codes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCodesWithPatch() throws Exception {
        // Initialize the database
        codesRepository.saveAndFlush(codes);

        int databaseSizeBeforeUpdate = codesRepository.findAll().size();

        // Update the codes using partial update
        Codes partialUpdatedCodes = new Codes();
        partialUpdatedCodes.setId(codes.getId());

        partialUpdatedCodes
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .units(UPDATED_UNITS)
            .enabled(UPDATED_ENABLED)
            .updatedAt(UPDATED_UPDATED_AT);

        restCodesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCodes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCodes))
            )
            .andExpect(status().isOk());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
        Codes testCodes = codesList.get(codesList.size() - 1);
        assertThat(testCodes.getMode()).isEqualTo(DEFAULT_MODE);
        assertThat(testCodes.getSegment()).isEqualTo(DEFAULT_SEGMENT);
        assertThat(testCodes.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCodes.getDimension()).isEqualTo(DEFAULT_DIMENSION);
        assertThat(testCodes.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCodes.getMinVal()).isEqualTo(DEFAULT_MIN_VAL);
        assertThat(testCodes.getMaxVal()).isEqualTo(DEFAULT_MAX_VAL);
        assertThat(testCodes.getUnits()).isEqualTo(UPDATED_UNITS);
        assertThat(testCodes.getEnabled()).isEqualTo(UPDATED_ENABLED);
        assertThat(testCodes.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testCodes.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateCodesWithPatch() throws Exception {
        // Initialize the database
        codesRepository.saveAndFlush(codes);

        int databaseSizeBeforeUpdate = codesRepository.findAll().size();

        // Update the codes using partial update
        Codes partialUpdatedCodes = new Codes();
        partialUpdatedCodes.setId(codes.getId());

        partialUpdatedCodes
            .mode(UPDATED_MODE)
            .segment(UPDATED_SEGMENT)
            .code(UPDATED_CODE)
            .dimension(UPDATED_DIMENSION)
            .description(UPDATED_DESCRIPTION)
            .minVal(UPDATED_MIN_VAL)
            .maxVal(UPDATED_MAX_VAL)
            .units(UPDATED_UNITS)
            .enabled(UPDATED_ENABLED)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restCodesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCodes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCodes))
            )
            .andExpect(status().isOk());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
        Codes testCodes = codesList.get(codesList.size() - 1);
        assertThat(testCodes.getMode()).isEqualTo(UPDATED_MODE);
        assertThat(testCodes.getSegment()).isEqualTo(UPDATED_SEGMENT);
        assertThat(testCodes.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCodes.getDimension()).isEqualTo(UPDATED_DIMENSION);
        assertThat(testCodes.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCodes.getMinVal()).isEqualTo(UPDATED_MIN_VAL);
        assertThat(testCodes.getMaxVal()).isEqualTo(UPDATED_MAX_VAL);
        assertThat(testCodes.getUnits()).isEqualTo(UPDATED_UNITS);
        assertThat(testCodes.getEnabled()).isEqualTo(UPDATED_ENABLED);
        assertThat(testCodes.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testCodes.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().size();
        codes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCodesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, codes.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(codes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().size();
        codes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCodesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(codes))
            )
            .andExpect(status().isBadRequest());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().size();
        codes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCodesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(codes)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCodes() throws Exception {
        // Initialize the database
        codesRepository.saveAndFlush(codes);

        int databaseSizeBeforeDelete = codesRepository.findAll().size();

        // Delete the codes
        restCodesMockMvc
            .perform(delete(ENTITY_API_URL_ID, codes.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Codes> codesList = codesRepository.findAll();
        assertThat(codesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
