package com.eventorback.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig {

	@Profile("dev")
	@Bean
	public static ElasticsearchClient devCreateElasticsearchClient() {
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

		RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));

		return new ElasticsearchClient(transport);
	}

	@Profile("prod")
	@Bean
	public static ElasticsearchClient pordCreateElasticsearchClient() {
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

		// 여러 노드로 구성된 클러스터 연결
		RestClient restClient = RestClient.builder(
			new HttpHost("es01-1", 9200),
			new HttpHost("es02-1", 9200),
			new HttpHost("es03-1", 9200)
		).build();

		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper));

		return new ElasticsearchClient(transport);
	}
}