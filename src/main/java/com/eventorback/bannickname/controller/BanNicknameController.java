package com.eventorback.bannickname.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.bannickname.domain.dto.BanNicknameDto;
import com.eventorback.bannickname.service.BanNicknameService;
import com.eventorback.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/banNicknames")
public class BanNicknameController {
	private final BanNicknameService banNicknameService;

	@AuthorizeRole("admin")
	@GetMapping
	public ResponseEntity<ApiResponse<List<BanNicknameDto>>> getBanNicknames() {
		return ApiResponse.createSuccess(banNicknameService.getBanNicknames());
	}

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<ApiResponse<Page<BanNicknameDto>>> getBanNicknames(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(banNicknameService.getBanNicknames(pageable));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createBanNickname(
		@RequestBody BanNicknameDto request) {
		banNicknameService.createBanNickname(request);
		return ApiResponse.createSuccess("금지 닉네임이 추가 되었습니다.");
	}

	@DeleteMapping("/{banNicknameId}")
	public ResponseEntity<ApiResponse<Void>> deleteBanNickname(@PathVariable Long banNicknameId) {
		banNicknameService.deleteBanNickname(banNicknameId);
		return ApiResponse.createSuccess("금지 닉네임이 제거 되었습니다.");
	}
}
