package com.eventorback.search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.eventorback.search.document.EsPost;

public interface ElasticSearchRepository extends ElasticsearchRepository<EsPost, Long> {
}
