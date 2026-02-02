package com.eventorback.oauth.repository;

import java.util.HashMap;
import java.util.Map;

import com.eventorback.oauth.config.OauthProvider;

public class InMemoryOauthRepository {
	private final Map<String, OauthProvider> oauthProviders;

	public InMemoryOauthRepository(Map<String, OauthProvider> oauthProviders) {
		this.oauthProviders = new HashMap<>(oauthProviders);
	}

	public OauthProvider findByProviderName(String name) {
		return oauthProviders.get(name);
	}
}
