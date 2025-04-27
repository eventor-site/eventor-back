package com.eventorback.postview.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.CurrentUser;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.postview.service.PostViewService;
import com.eventorback.user.domain.dto.CurrentUserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class PostViewController {
	private final PostViewService postViewService;

	@GetMapping("/users/me/postViews/paging")
	public ResponseEntity<ApiResponse<Page<GetMainPostResponse>>> getPosts(
		@CurrentUser CurrentUserDto currentUser, @RequestParam(required = false) String uuid,
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(postViewService.getPostViews(currentUser, uuid, pageable));
	}

}
