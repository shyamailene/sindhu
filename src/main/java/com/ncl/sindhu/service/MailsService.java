package com.ncl.sindhu.service;

import com.ncl.sindhu.domain.Mails;
import com.ncl.sindhu.repository.MailsRepository;
import com.ncl.sindhu.repository.search.MailsSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Mails.
 */
@Service
@Transactional
public class MailsService {

    private final Logger log = LoggerFactory.getLogger(MailsService.class);

    private final MailsRepository mailsRepository;

    private final MailsSearchRepository mailsSearchRepository;

    public MailsService(MailsRepository mailsRepository, MailsSearchRepository mailsSearchRepository) {
        this.mailsRepository = mailsRepository;
        this.mailsSearchRepository = mailsSearchRepository;
    }

    /**
     * Save a mails.
     *
     * @param mails the entity to save
     * @return the persisted entity
     */
    public Mails save(Mails mails) {
        log.debug("Request to save Mails : {}", mails);
        Mails result = mailsRepository.save(mails);
        mailsSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the mails.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Mails> findAll(Pageable pageable) {
        log.debug("Request to get all Mails");
        return mailsRepository.findAll(pageable);
    }

    /**
     *  Get one mails by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Mails findOne(Long id) {
        log.debug("Request to get Mails : {}", id);
        return mailsRepository.findOne(id);
    }

    /**
     *  Delete the  mails by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Mails : {}", id);
        mailsRepository.delete(id);
        mailsSearchRepository.delete(id);
    }

    /**
     * Search for the mails corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Mails> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Mails for query {}", query);
        Page<Mails> result = mailsSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
