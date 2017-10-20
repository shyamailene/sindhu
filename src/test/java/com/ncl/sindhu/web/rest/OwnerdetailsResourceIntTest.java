package com.ncl.sindhu.web.rest;

import com.ncl.sindhu.SindhuApp;

import com.ncl.sindhu.domain.Ownerdetails;
import com.ncl.sindhu.repository.OwnerdetailsRepository;
import com.ncl.sindhu.repository.search.OwnerdetailsSearchRepository;
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
 * Test class for the OwnerdetailsResource REST controller.
 *
 * @see OwnerdetailsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SindhuApp.class)
public class OwnerdetailsResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE = "BBBBBBBBBB";

    @Autowired
    private OwnerdetailsRepository ownerdetailsRepository;

    @Autowired
    private OwnerdetailsSearchRepository ownerdetailsSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOwnerdetailsMockMvc;

    private Ownerdetails ownerdetails;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OwnerdetailsResource ownerdetailsResource = new OwnerdetailsResource(ownerdetailsRepository, ownerdetailsSearchRepository);
        this.restOwnerdetailsMockMvc = MockMvcBuilders.standaloneSetup(ownerdetailsResource)
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
    public static Ownerdetails createEntity(EntityManager em) {
        Ownerdetails ownerdetails = new Ownerdetails()
            .name(DEFAULT_NAME)
            .mobile(DEFAULT_MOBILE);
        return ownerdetails;
    }

    @Before
    public void initTest() {
        ownerdetailsSearchRepository.deleteAll();
        ownerdetails = createEntity(em);
    }

    @Test
    @Transactional
    public void createOwnerdetails() throws Exception {
        int databaseSizeBeforeCreate = ownerdetailsRepository.findAll().size();

        // Create the Ownerdetails
        restOwnerdetailsMockMvc.perform(post("/api/ownerdetails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ownerdetails)))
            .andExpect(status().isCreated());

        // Validate the Ownerdetails in the database
        List<Ownerdetails> ownerdetailsList = ownerdetailsRepository.findAll();
        assertThat(ownerdetailsList).hasSize(databaseSizeBeforeCreate + 1);
        Ownerdetails testOwnerdetails = ownerdetailsList.get(ownerdetailsList.size() - 1);
        assertThat(testOwnerdetails.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOwnerdetails.getMobile()).isEqualTo(DEFAULT_MOBILE);

        // Validate the Ownerdetails in Elasticsearch
        Ownerdetails ownerdetailsEs = ownerdetailsSearchRepository.findOne(testOwnerdetails.getId());
        assertThat(ownerdetailsEs).isEqualToComparingFieldByField(testOwnerdetails);
    }

    @Test
    @Transactional
    public void createOwnerdetailsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ownerdetailsRepository.findAll().size();

        // Create the Ownerdetails with an existing ID
        ownerdetails.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOwnerdetailsMockMvc.perform(post("/api/ownerdetails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ownerdetails)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Ownerdetails> ownerdetailsList = ownerdetailsRepository.findAll();
        assertThat(ownerdetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ownerdetailsRepository.findAll().size();
        // set the field null
        ownerdetails.setName(null);

        // Create the Ownerdetails, which fails.

        restOwnerdetailsMockMvc.perform(post("/api/ownerdetails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ownerdetails)))
            .andExpect(status().isBadRequest());

        List<Ownerdetails> ownerdetailsList = ownerdetailsRepository.findAll();
        assertThat(ownerdetailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMobileIsRequired() throws Exception {
        int databaseSizeBeforeTest = ownerdetailsRepository.findAll().size();
        // set the field null
        ownerdetails.setMobile(null);

        // Create the Ownerdetails, which fails.

        restOwnerdetailsMockMvc.perform(post("/api/ownerdetails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ownerdetails)))
            .andExpect(status().isBadRequest());

        List<Ownerdetails> ownerdetailsList = ownerdetailsRepository.findAll();
        assertThat(ownerdetailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOwnerdetails() throws Exception {
        // Initialize the database
        ownerdetailsRepository.saveAndFlush(ownerdetails);

        // Get all the ownerdetailsList
        restOwnerdetailsMockMvc.perform(get("/api/ownerdetails?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ownerdetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.toString())));
    }

    @Test
    @Transactional
    public void getOwnerdetails() throws Exception {
        // Initialize the database
        ownerdetailsRepository.saveAndFlush(ownerdetails);

        // Get the ownerdetails
        restOwnerdetailsMockMvc.perform(get("/api/ownerdetails/{id}", ownerdetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(ownerdetails.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOwnerdetails() throws Exception {
        // Get the ownerdetails
        restOwnerdetailsMockMvc.perform(get("/api/ownerdetails/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOwnerdetails() throws Exception {
        // Initialize the database
        ownerdetailsRepository.saveAndFlush(ownerdetails);
        ownerdetailsSearchRepository.save(ownerdetails);
        int databaseSizeBeforeUpdate = ownerdetailsRepository.findAll().size();

        // Update the ownerdetails
        Ownerdetails updatedOwnerdetails = ownerdetailsRepository.findOne(ownerdetails.getId());
        updatedOwnerdetails
            .name(UPDATED_NAME)
            .mobile(UPDATED_MOBILE);

        restOwnerdetailsMockMvc.perform(put("/api/ownerdetails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOwnerdetails)))
            .andExpect(status().isOk());

        // Validate the Ownerdetails in the database
        List<Ownerdetails> ownerdetailsList = ownerdetailsRepository.findAll();
        assertThat(ownerdetailsList).hasSize(databaseSizeBeforeUpdate);
        Ownerdetails testOwnerdetails = ownerdetailsList.get(ownerdetailsList.size() - 1);
        assertThat(testOwnerdetails.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOwnerdetails.getMobile()).isEqualTo(UPDATED_MOBILE);

        // Validate the Ownerdetails in Elasticsearch
        Ownerdetails ownerdetailsEs = ownerdetailsSearchRepository.findOne(testOwnerdetails.getId());
        assertThat(ownerdetailsEs).isEqualToComparingFieldByField(testOwnerdetails);
    }

    @Test
    @Transactional
    public void updateNonExistingOwnerdetails() throws Exception {
        int databaseSizeBeforeUpdate = ownerdetailsRepository.findAll().size();

        // Create the Ownerdetails

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOwnerdetailsMockMvc.perform(put("/api/ownerdetails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ownerdetails)))
            .andExpect(status().isCreated());

        // Validate the Ownerdetails in the database
        List<Ownerdetails> ownerdetailsList = ownerdetailsRepository.findAll();
        assertThat(ownerdetailsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOwnerdetails() throws Exception {
        // Initialize the database
        ownerdetailsRepository.saveAndFlush(ownerdetails);
        ownerdetailsSearchRepository.save(ownerdetails);
        int databaseSizeBeforeDelete = ownerdetailsRepository.findAll().size();

        // Get the ownerdetails
        restOwnerdetailsMockMvc.perform(delete("/api/ownerdetails/{id}", ownerdetails.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean ownerdetailsExistsInEs = ownerdetailsSearchRepository.exists(ownerdetails.getId());
        assertThat(ownerdetailsExistsInEs).isFalse();

        // Validate the database is empty
        List<Ownerdetails> ownerdetailsList = ownerdetailsRepository.findAll();
        assertThat(ownerdetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOwnerdetails() throws Exception {
        // Initialize the database
        ownerdetailsRepository.saveAndFlush(ownerdetails);
        ownerdetailsSearchRepository.save(ownerdetails);

        // Search the ownerdetails
        restOwnerdetailsMockMvc.perform(get("/api/_search/ownerdetails?query=id:" + ownerdetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ownerdetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ownerdetails.class);
        Ownerdetails ownerdetails1 = new Ownerdetails();
        ownerdetails1.setId(1L);
        Ownerdetails ownerdetails2 = new Ownerdetails();
        ownerdetails2.setId(ownerdetails1.getId());
        assertThat(ownerdetails1).isEqualTo(ownerdetails2);
        ownerdetails2.setId(2L);
        assertThat(ownerdetails1).isNotEqualTo(ownerdetails2);
        ownerdetails1.setId(null);
        assertThat(ownerdetails1).isNotEqualTo(ownerdetails2);
    }
}
