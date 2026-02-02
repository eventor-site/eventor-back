package com.eventorback.oauth.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OauthProvider {
	private String clientId = "";
	private String clientSecret = "";
	private String redirectUri = "";
	private String authorizationGrantType = "";
	private String clientName = "";
	private String scope = "";

	private String authorizationUri = "";
	private String tokenUri = "";
	private String userInfoUri = "";
	private String userNameAttribute = "";

	public OauthProvider(OauthProperties.Registration registration, OauthProperties.Provider provider) {
		this(
			defaultIfNull(registration.getClientId(), ""),
			defaultIfNull(registration.getClientSecret(), ""),
			defaultIfNull(registration.getRedirectUri(), ""),
			defaultIfNull(registration.getAuthorizationGrantType(), ""),
			defaultIfNull(registration.getClientName(), ""),
			defaultIfNull(registration.getScope(), ""),

			defaultIfNull(provider.getAuthorizationUri(), ""),
			defaultIfNull(provider.getTokenUri(), ""),
			defaultIfNull(provider.getUserInfoUri(), ""),
			defaultIfNull(provider.getUserNameAttribute(), "")
		);
	}

	@Builder
	public OauthProvider(String clientId, String clientSecret, String redirectUrl, String authorizationGrantType
		, String clientName, String scope, String authorizationUri, String tokenUri, String userInfoUri,
		String userNameAttribute) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUrl;
		this.authorizationGrantType = authorizationGrantType;
		this.clientName = clientName;
		this.scope = scope;
		this.authorizationUri = authorizationUri;
		this.tokenUri = tokenUri;
		this.userInfoUri = userInfoUri;
		this.userNameAttribute = userNameAttribute;
	}

	private static String defaultIfNull(String value, String defaultValue) {
		return (value != null) ? value : defaultValue;
	}

}
