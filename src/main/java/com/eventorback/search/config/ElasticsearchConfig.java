package com.eventorback.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig {

	@Bean
	public static ElasticsearchClient createElasticsearchClient() {
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

		RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));

		return new ElasticsearchClient(transport);
	}
}