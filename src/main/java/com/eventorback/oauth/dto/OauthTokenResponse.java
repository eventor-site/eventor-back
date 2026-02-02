package com.eventorback.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OauthTokenResponse {
	@JsonProperty("access_token")
	private String accessToken;

	private String scope;

	@JsonProperty("id_token")
	private String idToken;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("expires_in")
	private Long expiresIn;

	@Builder
	public OauthTokenResponse(String accessToken, String scope, String tokenType, String idToken, Long expiresIn) {
		this.accessToken = accessToken;
		this.scope = scope;
		this.tokenType = tokenType;
		this.idToken = idToken;
		this.expiresIn = expiresIn;
	}

}
