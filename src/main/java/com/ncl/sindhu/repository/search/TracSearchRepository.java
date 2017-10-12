package com.ncl.sindhu.repository.search;

import com.ncl.sindhu.domain.Trac;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Trac entity.
 */
public interface TracSearchRepository extends ElasticsearchRepository<Trac, Long> {
}
