package com.ncl.sindhu.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ncl.sindhu.domain.Flat;

import com.ncl.sindhu.repository.FlatRepository;
import com.ncl.sindhu.repository.search.FlatSearchRepository;
import com.ncl.sindhu.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Flat.
 */
@RestController
@RequestMapping("/api")
public class FlatResource {

    private final Logger log = LoggerFactory.getLogger(FlatResource.class);

    private static final String ENTITY_NAME = "flat";

    private final FlatRepository flatRepository;

    private final FlatSearchRepository flatSearchRepository;

    public FlatResource(FlatRepository flatRepository, FlatSearchRepository flatSearchRepository) {
        this.flatRepository = flatRepository;
        this.flatSearchRepository = flatSearchRepository;
    }

    /**
     * POST  /flats : Create a new flat.
     *
     * @param flat the flat to create
     * @return the ResponseEntity with status 201 (Created) and with body the new flat, or with status 400 (Bad Request) if the flat has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/flats")
    @Timed
    public ResponseEntity<Flat> createFlat(@Valid @RequestBody Flat flat) throws URISyntaxException {
        log.debug("REST request to save Flat : {}", flat);
        if (flat.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new flat cannot already have an ID")).body(null);
        }
        Flat result = flatRepository.save(flat);
        flatSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/flats/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /flats : Updates an existing flat.
     *
     * @param flat the flat to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated flat,
     * or with status 400 (Bad Request) if the flat is not valid,
     * or with status 500 (Internal Server Error) if the flat couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/flats")
    @Timed
    public ResponseEntity<Flat> updateFlat(@Valid @RequestBody Flat flat) throws URISyntaxException {
        log.debug("REST request to update Flat : {}", flat);
        if (flat.getId() == null) {
            return createFlat(flat);
        }
        Flat result = flatRepository.save(flat);
        flatSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, flat.getId().toString()))
            .body(result);
    }

    /**
     * GET  /flats : get all the flats.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of flats in body
     */
    @GetMapping("/flats")
    @Timed
    public List<Flat> getAllFlats() {
        log.debug("REST request to get all Flats");
        return flatRepository.findAll();
    }

    /**
     * GET  /flats/:id : get the "id" flat.
     *
     * @param id the id of the flat to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the flat, or with status 404 (Not Found)
     */
    @GetMapping("/flats/{id}")
    @Timed
    public ResponseEntity<Flat> getFlat(@PathVariable Long id) {
        log.debug("REST request to get Flat : {}", id);
        Flat flat = flatRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(flat));
    }

    /**
     * DELETE  /flats/:id : delete the "id" flat.
     *
     * @param id the id of the flat to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/flats/{id}")
    @Timed
    public ResponseEntity<Void> deleteFlat(@PathVariable Long id) {
        log.debug("REST request to delete Flat : {}", id);
        flatRepository.delete(id);
        flatSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/flats?query=:query : search for the flat corresponding
     * to the query.
     *
     * @param query the query of the flat search
     * @return the result of the search
     */
    @GetMapping("/_search/flats")
    @Timed
    public List<Flat> searchFlats(@RequestParam String query) {
        log.debug("REST request to search Flats for query {}", query);
        return StreamSupport
            .stream(flatSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
