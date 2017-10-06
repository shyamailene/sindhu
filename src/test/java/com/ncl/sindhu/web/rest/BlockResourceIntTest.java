package com.ncl.sindhu.web.rest;

import com.ncl.sindhu.SindhuApp;

import com.ncl.sindhu.domain.Block;
import com.ncl.sindhu.repository.BlockRepository;
import com.ncl.sindhu.repository.search.BlockSearchRepository;
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
 * Test class for the BlockResource REST controller.
 *
 * @see BlockResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SindhuApp.class)
public class BlockResourceIntTest {

    private static final String DEFAULT_BLOCK_DESC = "AAAAAAAAAA";
    private static final String UPDATED_BLOCK_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_BLOCK_ID = "AAAAAAAAAA";
    private static final String UPDATED_BLOCK_ID = "BBBBBBBBBB";

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockSearchRepository blockSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBlockMockMvc;

    private Block block;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BlockResource blockResource = new BlockResource(blockRepository, blockSearchRepository);
        this.restBlockMockMvc = MockMvcBuilders.standaloneSetup(blockResource)
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
    public static Block createEntity(EntityManager em) {
        Block block = new Block()
            .blockDesc(DEFAULT_BLOCK_DESC)
            .blockId(DEFAULT_BLOCK_ID);
        return block;
    }

    @Before
    public void initTest() {
        blockSearchRepository.deleteAll();
        block = createEntity(em);
    }

    @Test
    @Transactional
    public void createBlock() throws Exception {
        int databaseSizeBeforeCreate = blockRepository.findAll().size();

        // Create the Block
        restBlockMockMvc.perform(post("/api/blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(block)))
            .andExpect(status().isCreated());

        // Validate the Block in the database
        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeCreate + 1);
        Block testBlock = blockList.get(blockList.size() - 1);
        assertThat(testBlock.getBlockDesc()).isEqualTo(DEFAULT_BLOCK_DESC);
        assertThat(testBlock.getBlockId()).isEqualTo(DEFAULT_BLOCK_ID);

        // Validate the Block in Elasticsearch
        Block blockEs = blockSearchRepository.findOne(testBlock.getId());
        assertThat(blockEs).isEqualToComparingFieldByField(testBlock);
    }

    @Test
    @Transactional
    public void createBlockWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = blockRepository.findAll().size();

        // Create the Block with an existing ID
        block.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlockMockMvc.perform(post("/api/blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(block)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkBlockIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockRepository.findAll().size();
        // set the field null
        block.setBlockId(null);

        // Create the Block, which fails.

        restBlockMockMvc.perform(post("/api/blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(block)))
            .andExpect(status().isBadRequest());

        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBlocks() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(block);

        // Get all the blockList
        restBlockMockMvc.perform(get("/api/blocks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(block.getId().intValue())))
            .andExpect(jsonPath("$.[*].blockDesc").value(hasItem(DEFAULT_BLOCK_DESC.toString())))
            .andExpect(jsonPath("$.[*].blockId").value(hasItem(DEFAULT_BLOCK_ID.toString())));
    }

    @Test
    @Transactional
    public void getBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(block);

        // Get the block
        restBlockMockMvc.perform(get("/api/blocks/{id}", block.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(block.getId().intValue()))
            .andExpect(jsonPath("$.blockDesc").value(DEFAULT_BLOCK_DESC.toString()))
            .andExpect(jsonPath("$.blockId").value(DEFAULT_BLOCK_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBlock() throws Exception {
        // Get the block
        restBlockMockMvc.perform(get("/api/blocks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(block);
        blockSearchRepository.save(block);
        int databaseSizeBeforeUpdate = blockRepository.findAll().size();

        // Update the block
        Block updatedBlock = blockRepository.findOne(block.getId());
        updatedBlock
            .blockDesc(UPDATED_BLOCK_DESC)
            .blockId(UPDATED_BLOCK_ID);

        restBlockMockMvc.perform(put("/api/blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBlock)))
            .andExpect(status().isOk());

        // Validate the Block in the database
        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
        Block testBlock = blockList.get(blockList.size() - 1);
        assertThat(testBlock.getBlockDesc()).isEqualTo(UPDATED_BLOCK_DESC);
        assertThat(testBlock.getBlockId()).isEqualTo(UPDATED_BLOCK_ID);

        // Validate the Block in Elasticsearch
        Block blockEs = blockSearchRepository.findOne(testBlock.getId());
        assertThat(blockEs).isEqualToComparingFieldByField(testBlock);
    }

    @Test
    @Transactional
    public void updateNonExistingBlock() throws Exception {
        int databaseSizeBeforeUpdate = blockRepository.findAll().size();

        // Create the Block

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBlockMockMvc.perform(put("/api/blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(block)))
            .andExpect(status().isCreated());

        // Validate the Block in the database
        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(block);
        blockSearchRepository.save(block);
        int databaseSizeBeforeDelete = blockRepository.findAll().size();

        // Get the block
        restBlockMockMvc.perform(delete("/api/blocks/{id}", block.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean blockExistsInEs = blockSearchRepository.exists(block.getId());
        assertThat(blockExistsInEs).isFalse();

        // Validate the database is empty
        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(block);
        blockSearchRepository.save(block);

        // Search the block
        restBlockMockMvc.perform(get("/api/_search/blocks?query=id:" + block.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(block.getId().intValue())))
            .andExpect(jsonPath("$.[*].blockDesc").value(hasItem(DEFAULT_BLOCK_DESC.toString())))
            .andExpect(jsonPath("$.[*].blockId").value(hasItem(DEFAULT_BLOCK_ID.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Block.class);
        Block block1 = new Block();
        block1.setId(1L);
        Block block2 = new Block();
        block2.setId(block1.getId());
        assertThat(block1).isEqualTo(block2);
        block2.setId(2L);
        assertThat(block1).isNotEqualTo(block2);
        block1.setId(null);
        assertThat(block1).isNotEqualTo(block2);
    }
}
