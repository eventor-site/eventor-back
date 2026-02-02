package com.eventorback.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventorback.global.dto.ApiResponse;
import com.eventorback.oauth.service.impl.OauthServiceImpl;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.response.OauthDto;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/back")
@RequiredArgsConstructor
public class OauthController {
	private final OauthServiceImpl oauthService;

	@GetMapping("/oauth2/authorization/{registrationId}")
	public ResponseEntity<ApiResponse<String>> authentication(@PathVariable String registrationId) {
		return ApiResponse.createSuccess(oauthService.authentication(registrationId), null);
	}

	@GetMapping("/oauth2/code/{registrationId}")
	public String getToken(@PathVariable String registrationId, @RequestParam String code,
		RedirectAttributes redirectAttributes, HttpServletResponse response) {

		SignUpRequest request = oauthService.getToken(registrationId, code);
		OauthDto oauthDto = new OauthDto(request.oauthId(), request.oauthType());
		boolean existsByOauth = oauthService.existsByOauth(oauthDto);

		if (existsByOauth) {
			oauthService.oauthLogin(oauthDto, response);
			return null;
		}

		// 이메일로 회원 가입된 아이디가 없는 경우
		redirectAttributes.addFlashAttribute("request", request);

		// nickname 입력 페이지로 리다이렉트
		return "redirect:/back/oauth2/signup";
	}

	@PostMapping("/oauth2/signup")
	public void oauthLogin(@ModelAttribute SignUpRequest request,
		HttpServletResponse response) {
		oauthService.oauthSignup(request);
		oauthService.oauthLogin(new OauthDto(request.oauthId(), request.oauthType()), response);
	}

}
