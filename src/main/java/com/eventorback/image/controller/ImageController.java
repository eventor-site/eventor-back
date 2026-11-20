package com.eventorback.image.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.auth.annotation.CurrentUser;
import com.eventorback.global.exception.ForbiddenException;
import com.eventorback.post.service.PostService;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.image.domain.dto.request.DeleteImageRequest;
import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.image.service.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
	private final ImageService imageService;
	private final PostService postService;

	@AuthorizeRole("member")
	@PostMapping(value = "/upload", consumes = "multipart/form-data")
	public ResponseEntity<ApiResponse<List<GetImageResponse>>> uploadImage(@CurrentUser CurrentUserDto currentUser,
		@RequestParam("file") MultipartFile file,
		@RequestParam String folderName, @RequestParam Long postId, @RequestParam String categoryName,
		@RequestParam boolean isThumbnail, @RequestParam boolean isPasted) {
		checkEditable(currentUser, postId);
		return ApiResponse.createSuccess(
			imageService.upload(file, folderName, postId, categoryName, isThumbnail, isPasted));
	}

	@AuthorizeRole("member")
	@DeleteMapping
	public ResponseEntity<ApiResponse<List<GetImageResponse>>> deleteImage(@CurrentUser CurrentUserDto currentUser,
		@RequestBody DeleteImageRequest request) {
		checkEditable(currentUser, request.postId());
		return ApiResponse.createSuccess(imageService.deleteImage(request));
	}

	@AuthorizeRole("member")
	@DeleteMapping("/temp")
	public ResponseEntity<ApiResponse<Void>> deleteTempImage(@CurrentUserId Long userId) {
		imageService.deleteTempImage(userId);
		return ApiResponse.createSuccess();
	}


	/** 글쓴이(또는 관리자)만 그 게시물의 이미지를 올리고 지울 수 있다 */
	private void checkEditable(CurrentUserDto currentUser, Long postId) {
		if (postId == null || !postService.isAuthorizedToEdit(currentUser, postId)) {
			throw new ForbiddenException("이 게시물의 이미지를 변경할 권한이 없습니다.");
		}
	}
}
