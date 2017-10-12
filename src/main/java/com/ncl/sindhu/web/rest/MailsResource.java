package com.ncl.sindhu.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ncl.sindhu.domain.Mails;
import com.ncl.sindhu.service.MailsService;
import com.ncl.sindhu.web.rest.util.HeaderUtil;
import com.ncl.sindhu.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Mails.
 */
@RestController
@RequestMapping("/api")
public class MailsResource {

    private final Logger log = LoggerFactory.getLogger(MailsResource.class);

    private static final String ENTITY_NAME = "mails";

    private final MailsService mailsService;

    public MailsResource(MailsService mailsService) {
        this.mailsService = mailsService;
    }

    /**
     * POST  /mails : Create a new mails.
     *
     * @param mails the mails to create
     * @return the ResponseEntity with status 201 (Created) and with body the new mails, or with status 400 (Bad Request) if the mails has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/mails")
    @Timed
    public ResponseEntity<Mails> createMails(@Valid @RequestBody Mails mails) throws URISyntaxException {
        log.debug("REST request to save Mails : {}", mails);
        if (mails.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new mails cannot already have an ID")).body(null);
        }
        Mails result = mailsService.save(mails);
        return ResponseEntity.created(new URI("/api/mails/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /mails : Updates an existing mails.
     *
     * @param mails the mails to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated mails,
     * or with status 400 (Bad Request) if the mails is not valid,
     * or with status 500 (Internal Server Error) if the mails couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/mails")
    @Timed
    public ResponseEntity<Mails> updateMails(@Valid @RequestBody Mails mails) throws URISyntaxException {
        log.debug("REST request to update Mails : {}", mails);
        if (mails.getId() == null) {
            return createMails(mails);
        }
        Mails result = mailsService.save(mails);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, mails.getId().toString()))
            .body(result);
    }

    /**
     * GET  /mails : get all the mails.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of mails in body
     */
    @GetMapping("/mails")
    @Timed
    public ResponseEntity<List<Mails>> getAllMails(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Mails");
        Page<Mails> page = mailsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/mails");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /mails/:id : get the "id" mails.
     *
     * @param id the id of the mails to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the mails, or with status 404 (Not Found)
     */
    @GetMapping("/mails/{id}")
    @Timed
    public ResponseEntity<Mails> getMails(@PathVariable Long id) {
        log.debug("REST request to get Mails : {}", id);
        Mails mails = mailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(mails));
    }

    /**
     * DELETE  /mails/:id : delete the "id" mails.
     *
     * @param id the id of the mails to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/mails/{id}")
    @Timed
    public ResponseEntity<Void> deleteMails(@PathVariable Long id) {
        log.debug("REST request to delete Mails : {}", id);
        mailsService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/mails?query=:query : search for the mails corresponding
     * to the query.
     *
     * @param query the query of the mails search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/mails")
    @Timed
    public ResponseEntity<List<Mails>> searchMails(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Mails for query {}", query);
        Page<Mails> page = mailsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/mails");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
