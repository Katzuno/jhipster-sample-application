package com.godrive.web.rest;

import static com.godrive.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.godrive.IntegrationTest;
import com.godrive.domain.Codes;
import com.godrive.repository.CodesRepository;
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
 * Integration tests for the {@link CodesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
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
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Codes.class).block();
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
        codes = createEntity(em);
    }

    @Test
    void createCodes() throws Exception {
        int databaseSizeBeforeCreate = codesRepository.findAll().collectList().block().size();
        // Create the Codes
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(codes))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
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
    void createCodesWithExistingId() throws Exception {
        // Create the Codes with an existing ID
        codes.setId(1L);

        int databaseSizeBeforeCreate = codesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(codes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
        assertThat(codesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCodesAsStream() {
        // Initialize the database
        codesRepository.save(codes).block();

        List<Codes> codesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Codes.class)
            .getResponseBody()
            .filter(codes::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(codesList).isNotNull();
        assertThat(codesList).hasSize(1);
        Codes testCodes = codesList.get(0);
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
    void getAllCodes() {
        // Initialize the database
        codesRepository.save(codes).block();

        // Get all the codesList
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
            .value(hasItem(codes.getId().intValue()))
            .jsonPath("$.[*].mode")
            .value(hasItem(DEFAULT_MODE))
            .jsonPath("$.[*].segment")
            .value(hasItem(DEFAULT_SEGMENT))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].dimension")
            .value(hasItem(DEFAULT_DIMENSION.doubleValue()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].minVal")
            .value(hasItem(DEFAULT_MIN_VAL))
            .jsonPath("$.[*].maxVal")
            .value(hasItem(DEFAULT_MAX_VAL))
            .jsonPath("$.[*].units")
            .value(hasItem(DEFAULT_UNITS))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED.booleanValue()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(sameInstant(DEFAULT_CREATED_AT)))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    void getCodes() {
        // Initialize the database
        codesRepository.save(codes).block();

        // Get the codes
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, codes.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(codes.getId().intValue()))
            .jsonPath("$.mode")
            .value(is(DEFAULT_MODE))
            .jsonPath("$.segment")
            .value(is(DEFAULT_SEGMENT))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.dimension")
            .value(is(DEFAULT_DIMENSION.doubleValue()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.minVal")
            .value(is(DEFAULT_MIN_VAL))
            .jsonPath("$.maxVal")
            .value(is(DEFAULT_MAX_VAL))
            .jsonPath("$.units")
            .value(is(DEFAULT_UNITS))
            .jsonPath("$.enabled")
            .value(is(DEFAULT_ENABLED.booleanValue()))
            .jsonPath("$.createdAt")
            .value(is(sameInstant(DEFAULT_CREATED_AT)))
            .jsonPath("$.updatedAt")
            .value(is(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    void getNonExistingCodes() {
        // Get the codes
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCodes() throws Exception {
        // Initialize the database
        codesRepository.save(codes).block();

        int databaseSizeBeforeUpdate = codesRepository.findAll().collectList().block().size();

        // Update the codes
        Codes updatedCodes = codesRepository.findById(codes.getId()).block();
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

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCodes.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCodes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
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
    void putNonExistingCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().collectList().block().size();
        codes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, codes.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(codes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().collectList().block().size();
        codes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(codes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().collectList().block().size();
        codes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(codes))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCodesWithPatch() throws Exception {
        // Initialize the database
        codesRepository.save(codes).block();

        int databaseSizeBeforeUpdate = codesRepository.findAll().collectList().block().size();

        // Update the codes using partial update
        Codes partialUpdatedCodes = new Codes();
        partialUpdatedCodes.setId(codes.getId());

        partialUpdatedCodes
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .units(UPDATED_UNITS)
            .enabled(UPDATED_ENABLED)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCodes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCodes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
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
    void fullUpdateCodesWithPatch() throws Exception {
        // Initialize the database
        codesRepository.save(codes).block();

        int databaseSizeBeforeUpdate = codesRepository.findAll().collectList().block().size();

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

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCodes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCodes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
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
    void patchNonExistingCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().collectList().block().size();
        codes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, codes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(codes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().collectList().block().size();
        codes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(codes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCodes() throws Exception {
        int databaseSizeBeforeUpdate = codesRepository.findAll().collectList().block().size();
        codes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(codes))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Codes in the database
        List<Codes> codesList = codesRepository.findAll().collectList().block();
        assertThat(codesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCodes() {
        // Initialize the database
        codesRepository.save(codes).block();

        int databaseSizeBeforeDelete = codesRepository.findAll().collectList().block().size();

        // Delete the codes
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, codes.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Codes> codesList = codesRepository.findAll().collectList().block();
        assertThat(codesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
