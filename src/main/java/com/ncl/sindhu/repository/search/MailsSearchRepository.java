package com.ncl.sindhu.repository.search;

import com.ncl.sindhu.domain.Mails;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Mails entity.
 */
public interface MailsSearchRepository extends ElasticsearchRepository<Mails, Long> {
}
