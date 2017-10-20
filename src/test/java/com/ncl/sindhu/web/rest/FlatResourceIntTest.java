package com.ncl.sindhu.web.rest;

import com.ncl.sindhu.SindhuApp;

import com.ncl.sindhu.domain.Flat;
import com.ncl.sindhu.domain.Block;
import com.ncl.sindhu.repository.FlatRepository;
import com.ncl.sindhu.repository.search.FlatSearchRepository;
import com.ncl.sindhu.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FlatResource REST controller.
 *
 * @see FlatResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SindhuApp.class)
public class FlatResourceIntTest {

    private static final String DEFAULT_FLAT_ID = "AAAAAAAAAA";
    private static final String UPDATED_FLAT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_FLAT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_FLAT_DESC = "BBBBBBBBBB";

    @Autowired
    private FlatRepository flatRepository;

    @Autowired
    private FlatSearchRepository flatSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restFlatMockMvc;

    private Flat flat;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FlatResource flatResource = new FlatResource(flatRepository, flatSearchRepository);
        this.restFlatMockMvc = MockMvcBuilders.standaloneSetup(flatResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Flat createEntity(EntityManager em) {
        Flat flat = new Flat()
            .flatId(DEFAULT_FLAT_ID)
            .flatDesc(DEFAULT_FLAT_DESC);
        // Add required entity
        Block blockflat = BlockResourceIntTest.createEntity(em);
        em.persist(blockflat);
        em.flush();
        flat.setBlockflat(blockflat);
        return flat;
    }

    @Before
    public void initTest() {
        flatSearchRepository.deleteAll();
        flat = createEntity(em);
    }

    @Test
    @Transactional
    public void createFlat() throws Exception {
        int databaseSizeBeforeCreate = flatRepository.findAll().size();

        // Create the Flat
        restFlatMockMvc.perform(post("/api/flats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(flat)))
            .andExpect(status().isCreated());

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll();
        assertThat(flatList).hasSize(databaseSizeBeforeCreate + 1);
        Flat testFlat = flatList.get(flatList.size() - 1);
        assertThat(testFlat.getFlatId()).isEqualTo(DEFAULT_FLAT_ID);
        assertThat(testFlat.getFlatDesc()).isEqualTo(DEFAULT_FLAT_DESC);

        // Validate the Flat in Elasticsearch
        Flat flatEs = flatSearchRepository.findOne(testFlat.getId());
        assertThat(flatEs).isEqualToComparingFieldByField(testFlat);
    }

    @Test
    @Transactional
    public void createFlatWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = flatRepository.findAll().size();

        // Create the Flat with an existing ID
        flat.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlatMockMvc.perform(post("/api/flats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(flat)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Flat> flatList = flatRepository.findAll();
        assertThat(flatList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkFlatIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = flatRepository.findAll().size();
        // set the field null
        flat.setFlatId(null);

        // Create the Flat, which fails.

        restFlatMockMvc.perform(post("/api/flats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(flat)))
            .andExpect(status().isBadRequest());

        List<Flat> flatList = flatRepository.findAll();
        assertThat(flatList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFlats() throws Exception {
        // Initialize the database
        flatRepository.saveAndFlush(flat);

        // Get all the flatList
        restFlatMockMvc.perform(get("/api/flats?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flat.getId().intValue())))
            .andExpect(jsonPath("$.[*].flatId").value(hasItem(DEFAULT_FLAT_ID.toString())))
            .andExpect(jsonPath("$.[*].flatDesc").value(hasItem(DEFAULT_FLAT_DESC.toString())));
    }

    @Test
    @Transactional
    public void getFlat() throws Exception {
        // Initialize the database
        flatRepository.saveAndFlush(flat);

        // Get the flat
        restFlatMockMvc.perform(get("/api/flats/{id}", flat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(flat.getId().intValue()))
            .andExpect(jsonPath("$.flatId").value(DEFAULT_FLAT_ID.toString()))
            .andExpect(jsonPath("$.flatDesc").value(DEFAULT_FLAT_DESC.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFlat() throws Exception {
        // Get the flat
        restFlatMockMvc.perform(get("/api/flats/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFlat() throws Exception {
        // Initialize the database
        flatRepository.saveAndFlush(flat);
        flatSearchRepository.save(flat);
        int databaseSizeBeforeUpdate = flatRepository.findAll().size();

        // Update the flat
        Flat updatedFlat = flatRepository.findOne(flat.getId());
        updatedFlat
            .flatId(UPDATED_FLAT_ID)
            .flatDesc(UPDATED_FLAT_DESC);

        restFlatMockMvc.perform(put("/api/flats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFlat)))
            .andExpect(status().isOk());

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate);
        Flat testFlat = flatList.get(flatList.size() - 1);
        assertThat(testFlat.getFlatId()).isEqualTo(UPDATED_FLAT_ID);
        assertThat(testFlat.getFlatDesc()).isEqualTo(UPDATED_FLAT_DESC);

        // Validate the Flat in Elasticsearch
        Flat flatEs = flatSearchRepository.findOne(testFlat.getId());
        assertThat(flatEs).isEqualToComparingFieldByField(testFlat);
    }

    @Test
    @Transactional
    public void updateNonExistingFlat() throws Exception {
        int databaseSizeBeforeUpdate = flatRepository.findAll().size();

        // Create the Flat

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restFlatMockMvc.perform(put("/api/flats")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(flat)))
            .andExpect(status().isCreated());

        // Validate the Flat in the database
        List<Flat> flatList = flatRepository.findAll();
        assertThat(flatList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteFlat() throws Exception {
        // Initialize the database
        flatRepository.saveAndFlush(flat);
        flatSearchRepository.save(flat);
        int databaseSizeBeforeDelete = flatRepository.findAll().size();

        // Get the flat
        restFlatMockMvc.perform(delete("/api/flats/{id}", flat.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean flatExistsInEs = flatSearchRepository.exists(flat.getId());
        assertThat(flatExistsInEs).isFalse();

        // Validate the database is empty
        List<Flat> flatList = flatRepository.findAll();
        assertThat(flatList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFlat() throws Exception {
        // Initialize the database
        flatRepository.saveAndFlush(flat);
        flatSearchRepository.save(flat);

        // Search the flat
        restFlatMockMvc.perform(get("/api/_search/flats?query=id:" + flat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flat.getId().intValue())))
            .andExpect(jsonPath("$.[*].flatId").value(hasItem(DEFAULT_FLAT_ID.toString())))
            .andExpect(jsonPath("$.[*].flatDesc").value(hasItem(DEFAULT_FLAT_DESC.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Flat.class);
        Flat flat1 = new Flat();
        flat1.setId(1L);
        Flat flat2 = new Flat();
        flat2.setId(flat1.getId());
        assertThat(flat1).isEqualTo(flat2);
        flat2.setId(2L);
        assertThat(flat1).isNotEqualTo(flat2);
        flat1.setId(null);
        assertThat(flat1).isNotEqualTo(flat2);
    }
}
