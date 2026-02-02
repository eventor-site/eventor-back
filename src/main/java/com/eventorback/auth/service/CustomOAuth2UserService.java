package com.eventorback.auth.service;// package com.eventorback.auth.service;
//
// import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
// import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
// import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.stereotype.Service;
//
// import com.eventorback.auth.client.UserClient;
// import com.eventorback.auth.dto.OAuth2Response;
// import com.eventorback.auth.dto.UserDTO;
// import com.eventorback.auth.dto.response.GetOauthResponse;
// import com.eventorback.auth.dto.response.GoogleResponse;
// import com.eventorback.auth.dto.response.NaverResponse;
//
// @Service
// public class CustomOAuth2UserService extends DefaultOAuth2UserService {
// 	private final UserClient userClient;
//
// 	public CustomOAuth2UserService(UserClient userClient) {
// 		this.userClient = userClient;
// 	}
//
// 	@Override
// 	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//
// 		OAuth2User oAuth2User = super.loadUser(userRequest);
// 		System.out.println(oAuth2User);
//
// 		String registrationId = userRequest.getClientRegistration().getRegistrationId();
// 		OAuth2Response oAuth2Response = null;
// 		if (registrationId.equals("naver")) {
// 			oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
// 		} else if (registrationId.equals("google")) {
// 			oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
// 		} else {
// 			return null;
// 		}
// 		String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
//
// 		String email = oAuth2Response.getEmail();
// 		GetOauthResponse existData = userClient.getOauthByEmail(email);
//
// 		if (existData == null) {
//
// 			// UserEntity userEntity = new UserEntity();
// 			// userEntity.setUsername(username);
// 			// userEntity.setEmail(oAuth2Response.getEmail());
// 			// userEntity.setName(oAuth2Response.getName());
// 			// userEntity.setRole("member");
// 			//
// 			// userRepository.save(userEntity);
//
// 			UserDTO userDTO = new UserDTO();
// 			userDTO.setUsername(username);
// 			userDTO.setName(oAuth2Response.getName());
// 			userDTO.setRole("member");
//
// 			return new CustomOAuth2User(userDTO);
// 		} else {
//
// 			// existData.setEmail(oAuth2Response.getEmail());
// 			// existData.setName(oAuth2Response.getName());
// 			//
// 			// userRepository.save(existData);
//
// 			UserDTO userDTO = new UserDTO();
// 			userDTO.setUsername(existData.username());
// 			userDTO.setName(oAuth2Response.getName());
// 			userDTO.setRole(existData.role());
//
// 			return new CustomOAuth2User(userDTO);
// 		}
// 	}
// }