package com.eventorback.oauth.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public class OauthProperties {
	private final Map<String, Registration> registration = new HashMap<>();
	private final Map<String, Provider> provider = new HashMap<>();

	@Getter
	@Setter
	public static class Registration {
		private String clientName;
		private String clientId;
		private String clientSecret;
		private String authorizationGrantType;
		private String redirectUri;
		private String scope;
	}

	@Getter
	@Setter
	public static class Provider {
		private String authorizationUri;
		private String tokenUri;
		private String userInfoUri;
		private String userNameAttribute;
	}
}
