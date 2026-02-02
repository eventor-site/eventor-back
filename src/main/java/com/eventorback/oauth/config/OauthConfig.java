package com.eventorback.oauth.config;

import java.util.Map;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eventorback.oauth.adapter.OauthAdapter;
import com.eventorback.oauth.repository.InMemoryOauthRepository;

@Configuration
@EnableConfigurationProperties(OauthProperties.class)
public class OauthConfig {

	private final OauthProperties properties;

	public OauthConfig(OauthProperties properties) {
		this.properties = properties;
	}

	@Bean
	public InMemoryOauthRepository inMemoryOauthRepository() {
		Map<String, OauthProvider> oauthProviders = OauthAdapter.getOauthProviders(properties);
		return new InMemoryOauthRepository(oauthProviders);
	}
}
