package com.ncl.sindhu.web.rest;

import com.ncl.sindhu.SindhuApp;

import com.ncl.sindhu.domain.Mails;
import com.ncl.sindhu.repository.MailsRepository;
import com.ncl.sindhu.service.MailsService;
import com.ncl.sindhu.repository.search.MailsSearchRepository;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MailsResource REST controller.
 *
 * @see MailsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SindhuApp.class)
public class MailsResourceIntTest {

    private static final String DEFAULT_FROM = "AAAAAAAAAA";
    private static final String UPDATED_FROM = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_MAIL = "AAAAAAAAAA";
    private static final String UPDATED_MAIL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_RECDATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RECDATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private MailsRepository mailsRepository;

    @Autowired
    private MailsService mailsService;

    @Autowired
    private MailsSearchRepository mailsSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMailsMockMvc;

    private Mails mails;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MailsResource mailsResource = new MailsResource(mailsService);
        this.restMailsMockMvc = MockMvcBuilders.standaloneSetup(mailsResource)
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
    public static Mails createEntity(EntityManager em) {
        Mails mails = new Mails()
            .from(DEFAULT_FROM)
            .subject(DEFAULT_SUBJECT)
            .mail(DEFAULT_MAIL)
            .recdate(DEFAULT_RECDATE);
        return mails;
    }

    @Before
    public void initTest() {
        mailsSearchRepository.deleteAll();
        mails = createEntity(em);
    }

    @Test
    @Transactional
    public void createMails() throws Exception {
        int databaseSizeBeforeCreate = mailsRepository.findAll().size();

        // Create the Mails
        restMailsMockMvc.perform(post("/api/mails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mails)))
            .andExpect(status().isCreated());

        // Validate the Mails in the database
        List<Mails> mailsList = mailsRepository.findAll();
        assertThat(mailsList).hasSize(databaseSizeBeforeCreate + 1);
        Mails testMails = mailsList.get(mailsList.size() - 1);
        assertThat(testMails.getFrom()).isEqualTo(DEFAULT_FROM);
        assertThat(testMails.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testMails.getMail()).isEqualTo(DEFAULT_MAIL);
        assertThat(testMails.getRecdate()).isEqualTo(DEFAULT_RECDATE);

        // Validate the Mails in Elasticsearch
        Mails mailsEs = mailsSearchRepository.findOne(testMails.getId());
        assertThat(mailsEs).isEqualToComparingFieldByField(testMails);
    }

    @Test
    @Transactional
    public void createMailsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = mailsRepository.findAll().size();

        // Create the Mails with an existing ID
        mails.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMailsMockMvc.perform(post("/api/mails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mails)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Mails> mailsList = mailsRepository.findAll();
        assertThat(mailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkFromIsRequired() throws Exception {
        int databaseSizeBeforeTest = mailsRepository.findAll().size();
        // set the field null
        mails.setFrom(null);

        // Create the Mails, which fails.

        restMailsMockMvc.perform(post("/api/mails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mails)))
            .andExpect(status().isBadRequest());

        List<Mails> mailsList = mailsRepository.findAll();
        assertThat(mailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = mailsRepository.findAll().size();
        // set the field null
        mails.setSubject(null);

        // Create the Mails, which fails.

        restMailsMockMvc.perform(post("/api/mails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mails)))
            .andExpect(status().isBadRequest());

        List<Mails> mailsList = mailsRepository.findAll();
        assertThat(mailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMailIsRequired() throws Exception {
        int databaseSizeBeforeTest = mailsRepository.findAll().size();
        // set the field null
        mails.setMail(null);

        // Create the Mails, which fails.

        restMailsMockMvc.perform(post("/api/mails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mails)))
            .andExpect(status().isBadRequest());

        List<Mails> mailsList = mailsRepository.findAll();
        assertThat(mailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRecdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = mailsRepository.findAll().size();
        // set the field null
        mails.setRecdate(null);

        // Create the Mails, which fails.

        restMailsMockMvc.perform(post("/api/mails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mails)))
            .andExpect(status().isBadRequest());

        List<Mails> mailsList = mailsRepository.findAll();
        assertThat(mailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMails() throws Exception {
        // Initialize the database
        mailsRepository.saveAndFlush(mails);

        // Get all the mailsList
        restMailsMockMvc.perform(get("/api/mails?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mails.getId().intValue())))
            .andExpect(jsonPath("$.[*].from").value(hasItem(DEFAULT_FROM.toString())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT.toString())))
            .andExpect(jsonPath("$.[*].mail").value(hasItem(DEFAULT_MAIL.toString())))
            .andExpect(jsonPath("$.[*].recdate").value(hasItem(DEFAULT_RECDATE.toString())));
    }

    @Test
    @Transactional
    public void getMails() throws Exception {
        // Initialize the database
        mailsRepository.saveAndFlush(mails);

        // Get the mails
        restMailsMockMvc.perform(get("/api/mails/{id}", mails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(mails.getId().intValue()))
            .andExpect(jsonPath("$.from").value(DEFAULT_FROM.toString()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT.toString()))
            .andExpect(jsonPath("$.mail").value(DEFAULT_MAIL.toString()))
            .andExpect(jsonPath("$.recdate").value(DEFAULT_RECDATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMails() throws Exception {
        // Get the mails
        restMailsMockMvc.perform(get("/api/mails/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMails() throws Exception {
        // Initialize the database
        mailsService.save(mails);

        int databaseSizeBeforeUpdate = mailsRepository.findAll().size();

        // Update the mails
        Mails updatedMails = mailsRepository.findOne(mails.getId());
        updatedMails
            .from(UPDATED_FROM)
            .subject(UPDATED_SUBJECT)
            .mail(UPDATED_MAIL)
            .recdate(UPDATED_RECDATE);

        restMailsMockMvc.perform(put("/api/mails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMails)))
            .andExpect(status().isOk());

        // Validate the Mails in the database
        List<Mails> mailsList = mailsRepository.findAll();
        assertThat(mailsList).hasSize(databaseSizeBeforeUpdate);
        Mails testMails = mailsList.get(mailsList.size() - 1);
        assertThat(testMails.getFrom()).isEqualTo(UPDATED_FROM);
        assertThat(testMails.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testMails.getMail()).isEqualTo(UPDATED_MAIL);
        assertThat(testMails.getRecdate()).isEqualTo(UPDATED_RECDATE);

        // Validate the Mails in Elasticsearch
        Mails mailsEs = mailsSearchRepository.findOne(testMails.getId());
        assertThat(mailsEs).isEqualToComparingFieldByField(testMails);
    }

    @Test
    @Transactional
    public void updateNonExistingMails() throws Exception {
        int databaseSizeBeforeUpdate = mailsRepository.findAll().size();

        // Create the Mails

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMailsMockMvc.perform(put("/api/mails")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mails)))
            .andExpect(status().isCreated());

        // Validate the Mails in the database
        List<Mails> mailsList = mailsRepository.findAll();
        assertThat(mailsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMails() throws Exception {
        // Initialize the database
        mailsService.save(mails);

        int databaseSizeBeforeDelete = mailsRepository.findAll().size();

        // Get the mails
        restMailsMockMvc.perform(delete("/api/mails/{id}", mails.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean mailsExistsInEs = mailsSearchRepository.exists(mails.getId());
        assertThat(mailsExistsInEs).isFalse();

        // Validate the database is empty
        List<Mails> mailsList = mailsRepository.findAll();
        assertThat(mailsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchMails() throws Exception {
        // Initialize the database
        mailsService.save(mails);

        // Search the mails
        restMailsMockMvc.perform(get("/api/_search/mails?query=id:" + mails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mails.getId().intValue())))
            .andExpect(jsonPath("$.[*].from").value(hasItem(DEFAULT_FROM.toString())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT.toString())))
            .andExpect(jsonPath("$.[*].mail").value(hasItem(DEFAULT_MAIL.toString())))
            .andExpect(jsonPath("$.[*].recdate").value(hasItem(DEFAULT_RECDATE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mails.class);
        Mails mails1 = new Mails();
        mails1.setId(1L);
        Mails mails2 = new Mails();
        mails2.setId(mails1.getId());
        assertThat(mails1).isEqualTo(mails2);
        mails2.setId(2L);
        assertThat(mails1).isNotEqualTo(mails2);
        mails1.setId(null);
        assertThat(mails1).isNotEqualTo(mails2);
    }
}
