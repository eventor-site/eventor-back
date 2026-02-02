// package com.eventorback.auth.client;
//
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
//
// import com.eventorback.global.dto.ApiResponse;
// import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
// import com.eventorback.user.domain.dto.request.CheckNicknameRequest;
// import com.eventorback.user.domain.dto.request.SignUpRequest;
// import com.eventorback.user.domain.dto.request.UpdateLoginAtRequest;
// import com.eventorback.user.domain.dto.response.GetUserAuth;
// import com.eventorback.user.domain.dto.response.GetUserOauth;
// import com.eventorback.user.domain.dto.response.OauthDto;
//
// @FeignClient(name = "user-client", url = "${feignClient.url}")
// public interface UserClient {
//
// 	@GetMapping("/back/users/auth/info")
// 	ResponseEntity<ApiResponse<GetUserAuth>> getAuthInfoByIdentifier(@RequestParam String identifier);
//
// 	@PostMapping("/back/users/oauth2/info")
// 	ResponseEntity<ApiResponse<GetUserOauth>> getOAuthInfoByOauth(@RequestBody OauthDto request);
//
// 	@PostMapping("/back/users/signup")
// 	ResponseEntity<ApiResponse<Void>> oauthSignup(@RequestBody SignUpRequest request);
//
// 	@PostMapping("/back/users/signup/checkIdentifier")
// 	ResponseEntity<ApiResponse<Void>> checkIdentifier(@RequestBody CheckIdentifierRequest request);
//
// 	@PostMapping("/back/users/signup/oauth2/exists")
// 	ResponseEntity<ApiResponse<Boolean>> existsByOauth(@RequestBody OauthDto request);
//
// 	@PostMapping("/back/users/signup/checkNickname")
// 	ResponseEntity<ApiResponse<Void>> checkNickname(@RequestBody CheckNicknameRequest request);
//
// 	@PutMapping("/back/users/me/loginAt")
// 	ResponseEntity<ApiResponse<Void>> updateLoginAt(UpdateLoginAtRequest request);
// }
