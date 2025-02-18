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

	// @Profile("prod")
	// @Bean
	// public static ElasticsearchClient prodCreateElasticsearchClient() {
	// 	try {
	// 		// ObjectMapper 설정
	// 		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
	//
	// 		// 자격 증명 제공자 설정 (사용자 이름과 비밀번호)
	// 		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
	// 		// 각 호스트에 대해 동일한 자격증명을 사용할 경우, 아래와 같이 설정합니다.
	// 		credentialsProvider.setCredentials(
	// 			new AuthScope("elasticsearch-es01-1", 9200),
	// 			new UsernamePasswordCredentials("elastic", "elastic")
	// 		);
	// 		// 필요에 따라 다른 호스트도 추가할 수 있지만, 기본적으로 첫 번째 호스트에 설정된 자격증명이 적용됩니다.
	//
	// 		// 모든 SSL 인증서를 신뢰하는 TrustManager 생성 (보안 위험: 테스트용으로만 사용)
	// 		TrustManager[] trustAllCerts = new TrustManager[] {
	// 			new X509TrustManager() {
	// 				@Override
	// 				public void checkClientTrusted(X509Certificate[] chain, String authType) {
	// 					// 아무 것도 하지 않음
	// 				}
	//
	// 				@Override
	// 				public void checkServerTrusted(X509Certificate[] chain, String authType) {
	// 					// 아무 것도 하지 않음
	// 				}
	//
	// 				@Override
	// 				public X509Certificate[] getAcceptedIssuers() {
	// 					return new X509Certificate[0];
	// 				}
	// 			}
	// 		};
	//
	// 		// SSLContext 생성 및 초기화 (모든 인증서를 신뢰)
	// 		SSLContext sslContext = SSLContext.getInstance("TLS");
	// 		sslContext.init(null, trustAllCerts, new SecureRandom());
	//
	// 		// RestClientBuilder에 SSLContext와 NoopHostnameVerifier, 그리고 자격증명 제공자를 설정
	// 		RestClientBuilder restClientBuilder = RestClient.builder(
	// 			new HttpHost("elasticsearch-es01-1", 9200, "https"),
	// 			new HttpHost("elasticsearch-es02-1", 9200, "https"),
	// 			new HttpHost("elasticsearch-es03-1", 9200, "https")
	// 		).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
	// 			@Override
	// 			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
	// 				return httpClientBuilder
	// 					.setSSLContext(sslContext)
	// 					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
	// 					.setDefaultCredentialsProvider(credentialsProvider);
	// 			}
	// 		});
	//
	// 		// RestClient 및 ElasticsearchTransport 설정
	// 		RestClient restClient = restClientBuilder.build();
	// 		ElasticsearchTransport transport = new RestClientTransport(restClient,
	// 			new JacksonJsonpMapper(objectMapper));
	//
	// 		// ElasticsearchClient 생성 및 반환
	// 		return new ElasticsearchClient(transport);
	//
	// 	} catch (Exception e) {
	// 		throw new RuntimeException("Elasticsearch 클라이언트 생성 실패", e);
	// 	}
	// }
}