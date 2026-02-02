package com.eventorback.oauth.adapter;

import java.util.HashMap;
import java.util.Map;

import com.eventorback.oauth.config.OauthProperties;
import com.eventorback.oauth.config.OauthProvider;

public class OauthAdapter {
	private OauthAdapter() {
	}

	public static Map<String, OauthProvider> getOauthProviders(OauthProperties properties) {
		Map<String, OauthProvider> oauthProvider = new HashMap<>();

		properties.getRegistration().forEach((key, value) -> oauthProvider.put(key,
			new OauthProvider(value, properties.getProvider().get(key))));
		return oauthProvider;
	}

	// public static Map<String, OauthClient> getOauthClients(OauthProperties properties) {
	// 	return properties.getRegistration().entrySet().stream()
	// 		.collect(Collectors.toMap(
	// 			Map.Entry::getKey,
	// 			entry -> new OauthClient(
	// 				entry.getValue(),
	// 				properties.getProvider().getOrDefault(entry.getKey(), new OauthProperties.Provider())
	// 			)
	// 		));
	// }
}
