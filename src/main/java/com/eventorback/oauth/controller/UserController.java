// package com.eventorback.oauth.controller;
//
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
//
// import com.eventorback.auth.dto.request.CheckNicknameRequest;
// import com.eventorback.auth.dto.request.SignUpRequest;
// import com.eventorback.oauth.service.impl.UserServiceImpl;
//
// import lombok.RequiredArgsConstructor;
//
// @Controller
// @RequestMapping("/back")
// @RequiredArgsConstructor
// public class UserController {
// 	private final UserServiceImpl userService;
//
// 	@GetMapping("/oauth2/signup")
// 	public String nickname(@ModelAttribute("request") SignUpRequest request, Model model) {
// 		model.addAttribute("request", request); // request 정보를 모델에 추가하여 뷰에서 사용할 수 있게 함
// 		return "oauth/nickname";
// 	}
//
// 	@PostMapping("/oauth2/signup/checkNickname")
// 	public ResponseEntity<String> checkNickname(@ModelAttribute CheckNicknameRequest request) {
// 		return ResponseEntity.ok(userService.checkNickname(request).getMessage());
// 	}
// }
