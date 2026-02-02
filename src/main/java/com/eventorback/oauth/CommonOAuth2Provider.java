package com.eventorback.oauth;// package com.eventorback.oauth;
//
// public enum CustomOAuth2Provider {
//
// 	GOOGLE {
// 		public OauthProvider getBuilder(String registrationId) {
// 			return getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
// 				.clientName("Google")
// 				.clientId("")
// 				.clientSerect("")
// 				.redirectUri("")
// 				.scope("profile", "email")
// 				.authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
// 				.tokenUri("https://accounts.googleapis.com/oauth2/v4/token")
// 				.userInfoUri("https://accounts.googleapis.com/oauth2/v3/userinfo")
// 				.userNameAttributeName("sub");
// 		}
// 	},
// 	NAVER {
// 		public OauthProvider getBuilder(String registrationId) {
// 			return getBuilder(registrationId, ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
// 				.scope("profile", "email")
// 				.authorizationUri("https://nid.naver.com/oauth2.0/authorize")
// 				.tokenUri("https://nid.naver.com/oauth2.0/token")
// 				.userInfoUri("https://openapi.naver.com/v1/nid/me")
// 				.userNameAttributeName("response.id")
// 				.clientName("Naver");
// 		}
// 	},
// 	KAKAO {
// 		public OauthProvider getBuilder(String registrationId) {
// 			return getBuilder(registrationId)
// 				.scope("profile", "account_email")
// 				.authorizationUri("https://kauth.kakao.com/oauth/authorize")
// 				.tokenUri("https://kauth.kakao.com/oauth/token")
// 				.userInfoUri("https://kapi.kakao.com/v2/user/me")
// 				.userNameAttributeName("id")
// 				.clientName("Kakao");
// 		}
// 	};
//
// }
