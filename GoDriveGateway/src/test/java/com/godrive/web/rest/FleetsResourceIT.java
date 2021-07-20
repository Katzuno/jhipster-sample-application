package com.godrive.web.rest;

import static com.godrive.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.godrive.IntegrationTest;
import com.godrive.domain.Fleets;
import com.godrive.repository.FleetsRepository;
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
 * Integration tests for the {@link FleetsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
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
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Fleets.class).block();
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
        fleets = createEntity(em);
    }

    @Test
    void createFleets() throws Exception {
        int databaseSizeBeforeCreate = fleetsRepository.findAll().collectList().block().size();
        // Create the Fleets
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fleets))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeCreate + 1);
        Fleets testFleets = fleetsList.get(fleetsList.size() - 1);
        assertThat(testFleets.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFleets.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFleets.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testFleets.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    void createFleetsWithExistingId() throws Exception {
        // Create the Fleets with an existing ID
        fleets.setId(1L);

        int databaseSizeBeforeCreate = fleetsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fleets))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllFleetsAsStream() {
        // Initialize the database
        fleetsRepository.save(fleets).block();

        List<Fleets> fleetsList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Fleets.class)
            .getResponseBody()
            .filter(fleets::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(fleetsList).isNotNull();
        assertThat(fleetsList).hasSize(1);
        Fleets testFleets = fleetsList.get(0);
        assertThat(testFleets.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFleets.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFleets.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testFleets.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    void getAllFleets() {
        // Initialize the database
        fleetsRepository.save(fleets).block();

        // Get all the fleetsList
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
            .value(hasItem(fleets.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(sameInstant(DEFAULT_CREATED_AT)))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    void getFleets() {
        // Initialize the database
        fleetsRepository.save(fleets).block();

        // Get the fleets
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, fleets.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(fleets.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.createdAt")
            .value(is(sameInstant(DEFAULT_CREATED_AT)))
            .jsonPath("$.updatedAt")
            .value(is(sameInstant(DEFAULT_UPDATED_AT)));
    }

    @Test
    void getNonExistingFleets() {
        // Get the fleets
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFleets() throws Exception {
        // Initialize the database
        fleetsRepository.save(fleets).block();

        int databaseSizeBeforeUpdate = fleetsRepository.findAll().collectList().block().size();

        // Update the fleets
        Fleets updatedFleets = fleetsRepository.findById(fleets.getId()).block();
        updatedFleets.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedFleets.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedFleets))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
        Fleets testFleets = fleetsList.get(fleetsList.size() - 1);
        assertThat(testFleets.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFleets.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFleets.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testFleets.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    void putNonExistingFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().collectList().block().size();
        fleets.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, fleets.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fleets))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().collectList().block().size();
        fleets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fleets))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().collectList().block().size();
        fleets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(fleets))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFleetsWithPatch() throws Exception {
        // Initialize the database
        fleetsRepository.save(fleets).block();

        int databaseSizeBeforeUpdate = fleetsRepository.findAll().collectList().block().size();

        // Update the fleets using partial update
        Fleets partialUpdatedFleets = new Fleets();
        partialUpdatedFleets.setId(fleets.getId());

        partialUpdatedFleets.createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFleets.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFleets))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
        Fleets testFleets = fleetsList.get(fleetsList.size() - 1);
        assertThat(testFleets.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFleets.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFleets.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testFleets.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    void fullUpdateFleetsWithPatch() throws Exception {
        // Initialize the database
        fleetsRepository.save(fleets).block();

        int databaseSizeBeforeUpdate = fleetsRepository.findAll().collectList().block().size();

        // Update the fleets using partial update
        Fleets partialUpdatedFleets = new Fleets();
        partialUpdatedFleets.setId(fleets.getId());

        partialUpdatedFleets
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFleets.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFleets))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
        Fleets testFleets = fleetsList.get(fleetsList.size() - 1);
        assertThat(testFleets.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFleets.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFleets.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testFleets.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    void patchNonExistingFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().collectList().block().size();
        fleets.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, fleets.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(fleets))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().collectList().block().size();
        fleets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(fleets))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFleets() throws Exception {
        int databaseSizeBeforeUpdate = fleetsRepository.findAll().collectList().block().size();
        fleets.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(fleets))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Fleets in the database
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFleets() {
        // Initialize the database
        fleetsRepository.save(fleets).block();

        int databaseSizeBeforeDelete = fleetsRepository.findAll().collectList().block().size();

        // Delete the fleets
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, fleets.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Fleets> fleetsList = fleetsRepository.findAll().collectList().block();
        assertThat(fleetsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
