package com.eventorback.search.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
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
		// ObjectMapper 설정
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

		// 자격 증명 제공자 설정 (사용자 이름과 비밀번호)
		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(
			new AuthScope("elasticsearch-es01-1", 9200),
			new UsernamePasswordCredentials("elastic", "elastic") // username, password
		);

		// RestClient 설정
		RestClientBuilder restClientBuilder = RestClient.builder(
			new HttpHost("elasticsearch-es01-1", 9200, "http"),
			new HttpHost("elasticsearch-es02-1", 9200, "http"),
			new HttpHost("elasticsearch-es03-1", 9200, "http")
		).setHttpClientConfigCallback(httpClientBuilder ->
			httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

		// RestClientTransport 설정
		RestClient restClient = restClientBuilder.build();
		ElasticsearchTransport transport = new RestClientTransport(
			restClient,
			new JacksonJsonpMapper(objectMapper)
		);

		// ElasticsearchClient 생성 및 반환
		return new ElasticsearchClient(transport);
	}
}