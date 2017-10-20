package com.ncl.sindhu.repository.search;

import com.ncl.sindhu.domain.Ownerdetails;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Ownerdetails entity.
 */
public interface OwnerdetailsSearchRepository extends ElasticsearchRepository<Ownerdetails, Long> {
}
