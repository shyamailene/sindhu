package com.ncl.sindhu.web.rest;

import com.ncl.sindhu.SindhuApp;

import com.ncl.sindhu.domain.Trac;
import com.ncl.sindhu.repository.TracRepository;
import com.ncl.sindhu.repository.UserRepository;
import com.ncl.sindhu.repository.search.TracSearchRepository;
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
 * Test class for the TracResource REST controller.
 *
 * @see TracResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SindhuApp.class)
public class TracResourceIntTest {

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private TracRepository tracRepository;

    @Autowired
    private TracSearchRepository tracSearchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTracMockMvc;

    private Trac trac;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TracResource tracResource = new TracResource(tracRepository, tracSearchRepository,userRepository);
        this.restTracMockMvc = MockMvcBuilders.standaloneSetup(tracResource)
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
    public static Trac createEntity(EntityManager em) {
        Trac trac = new Trac()
            .subject(DEFAULT_SUBJECT)
            .type(DEFAULT_TYPE)
            .description(DEFAULT_DESCRIPTION);
        return trac;
    }

    @Before
    public void initTest() {
        tracSearchRepository.deleteAll();
        trac = createEntity(em);
    }

    @Test
    @Transactional
    public void createTrac() throws Exception {
        int databaseSizeBeforeCreate = tracRepository.findAll().size();

        // Create the Trac
        restTracMockMvc.perform(post("/api/tracs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trac)))
            .andExpect(status().isCreated());

        // Validate the Trac in the database
        List<Trac> tracList = tracRepository.findAll();
        assertThat(tracList).hasSize(databaseSizeBeforeCreate + 1);
        Trac testTrac = tracList.get(tracList.size() - 1);
        assertThat(testTrac.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testTrac.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTrac.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Trac in Elasticsearch
        Trac tracEs = tracSearchRepository.findOne(testTrac.getId());
        assertThat(tracEs).isEqualToComparingFieldByField(testTrac);
    }

    @Test
    @Transactional
    public void createTracWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tracRepository.findAll().size();

        // Create the Trac with an existing ID
        trac.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTracMockMvc.perform(post("/api/tracs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trac)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Trac> tracList = tracRepository.findAll();
        assertThat(tracList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = tracRepository.findAll().size();
        // set the field null
        trac.setSubject(null);

        // Create the Trac, which fails.

        restTracMockMvc.perform(post("/api/tracs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trac)))
            .andExpect(status().isBadRequest());

        List<Trac> tracList = tracRepository.findAll();
        assertThat(tracList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTracs() throws Exception {
        // Initialize the database
        tracRepository.saveAndFlush(trac);

        // Get all the tracList
        restTracMockMvc.perform(get("/api/tracs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trac.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getTrac() throws Exception {
        // Initialize the database
        tracRepository.saveAndFlush(trac);

        // Get the trac
        restTracMockMvc.perform(get("/api/tracs/{id}", trac.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(trac.getId().intValue()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTrac() throws Exception {
        // Get the trac
        restTracMockMvc.perform(get("/api/tracs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTrac() throws Exception {
        // Initialize the database
        tracRepository.saveAndFlush(trac);
        tracSearchRepository.save(trac);
        int databaseSizeBeforeUpdate = tracRepository.findAll().size();

        // Update the trac
        Trac updatedTrac = tracRepository.findOne(trac.getId());
        updatedTrac
            .subject(UPDATED_SUBJECT)
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION);

        restTracMockMvc.perform(put("/api/tracs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTrac)))
            .andExpect(status().isOk());

        // Validate the Trac in the database
        List<Trac> tracList = tracRepository.findAll();
        assertThat(tracList).hasSize(databaseSizeBeforeUpdate);
        Trac testTrac = tracList.get(tracList.size() - 1);
        assertThat(testTrac.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testTrac.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTrac.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Trac in Elasticsearch
        Trac tracEs = tracSearchRepository.findOne(testTrac.getId());
        assertThat(tracEs).isEqualToComparingFieldByField(testTrac);
    }

    @Test
    @Transactional
    public void updateNonExistingTrac() throws Exception {
        int databaseSizeBeforeUpdate = tracRepository.findAll().size();

        // Create the Trac

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTracMockMvc.perform(put("/api/tracs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trac)))
            .andExpect(status().isCreated());

        // Validate the Trac in the database
        List<Trac> tracList = tracRepository.findAll();
        assertThat(tracList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTrac() throws Exception {
        // Initialize the database
        tracRepository.saveAndFlush(trac);
        tracSearchRepository.save(trac);
        int databaseSizeBeforeDelete = tracRepository.findAll().size();

        // Get the trac
        restTracMockMvc.perform(delete("/api/tracs/{id}", trac.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean tracExistsInEs = tracSearchRepository.exists(trac.getId());
        assertThat(tracExistsInEs).isFalse();

        // Validate the database is empty
        List<Trac> tracList = tracRepository.findAll();
        assertThat(tracList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTrac() throws Exception {
        // Initialize the database
        tracRepository.saveAndFlush(trac);
        tracSearchRepository.save(trac);

        // Search the trac
        restTracMockMvc.perform(get("/api/_search/tracs?query=id:" + trac.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trac.getId().intValue())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trac.class);
        Trac trac1 = new Trac();
        trac1.setId(1L);
        Trac trac2 = new Trac();
        trac2.setId(trac1.getId());
        assertThat(trac1).isEqualTo(trac2);
        trac2.setId(2L);
        assertThat(trac1).isNotEqualTo(trac2);
        trac1.setId(null);
        assertThat(trac1).isNotEqualTo(trac2);
    }
}
