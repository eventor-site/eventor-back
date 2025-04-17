package com.eventorback.post.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.auth.annotation.CurrentUser;
import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetEventPostCountByAdminResponse;
import com.eventorback.post.domain.dto.response.GetMainHotPostResponse;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.dto.response.GetRecommendPostResponse;
import com.eventorback.post.domain.dto.response.GetTempPostResponse;
import com.eventorback.post.service.PostService;
import com.eventorback.search.document.dto.reponse.SearchPostsResponse;
import com.eventorback.search.service.ElasticSearchService;
import com.eventorback.user.domain.dto.CurrentUserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/posts")
public class PostController {
	private final PostService postService;
	private final ElasticSearchService elasticSearchService;

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<Page<SearchPostsResponse>>> searchPosts(
		@PageableDefault(page = 1, size = 10, sort = "createdAt,desc") Pageable pageable,
		@RequestParam(defaultValue = "") String keyword,
		@RequestParam(defaultValue = "") String categoryName,
		@RequestParam(defaultValue = "") String eventStatusName,
		@RequestParam(defaultValue = "") String endType) {
		return ApiResponse.createSuccess(
			elasticSearchService.searchPosts(pageable, keyword, categoryName, eventStatusName, endType));
	}

	@AuthorizeRole("admin")
	@GetMapping("/all/paging")
	public ResponseEntity<ApiResponse<Page<GetPostSimpleResponse>>> getPosts(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(postService.getPosts(pageable));
	}

	@AuthorizeRole("admin")
	@GetMapping("/monitor/paging")
	public ResponseEntity<ApiResponse<Page<GetPostSimpleResponse>>> monitorPosts(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(postService.monitorPosts(pageable));
	}

	@GetMapping("/event/hot")
	public ResponseEntity<ApiResponse<List<GetMainHotPostResponse>>> getHotEventPosts() {
		return ApiResponse.createSuccess(postService.getHotEventPosts());
	}

	@GetMapping("/event/latest")
	public ResponseEntity<ApiResponse<List<GetMainPostResponse>>> getLatestEventPosts() {
		return ApiResponse.createSuccess(postService.getLatestEventPosts());
	}

	@GetMapping("/event/deadline")
	public ResponseEntity<ApiResponse<List<GetMainPostResponse>>> getDeadlineEventPosts() {
		return ApiResponse.createSuccess(postService.getDeadlineEventPosts());
	}

	@GetMapping("/event/recommendation")
	public ResponseEntity<ApiResponse<List<GetRecommendPostResponse>>> getRecommendationEventPosts() {
		return ApiResponse.createSuccess(postService.getRecommendationEventPosts());
	}

	@GetMapping("/event/trending")
	ResponseEntity<ApiResponse<List<GetRecommendPostResponse>>> getTrendingEventPosts() {
		return ApiResponse.createSuccess(postService.getTrendingEventPosts());
	}

	@GetMapping("/community")
	public ResponseEntity<ApiResponse<List<GetMainPostResponse>>> getCommunityPosts() {
		return ApiResponse.createSuccess(postService.getCommunityPosts());
	}

	@GetMapping("/hot")
	public ResponseEntity<ApiResponse<List<GetMainPostResponse>>> getHotPostsByCategoryName(
		@CurrentUser CurrentUserDto currentUser,
		@RequestParam String categoryName) {
		return ApiResponse.createSuccess(postService.getHotPostsByCategoryName(currentUser, categoryName));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Page<GetPostsByCategoryNameResponse>>> getPostsByCategoryName(
		@PageableDefault(page = 1, size = 10, sort = "createdAt,desc") Pageable pageable,
		@RequestParam(defaultValue = "") String categoryName,
		@RequestParam(defaultValue = "") String eventStatusName,
		@RequestParam(defaultValue = "") String endType) {
		return ApiResponse.createSuccess(
			postService.getPostsByCategoryName(pageable, categoryName, eventStatusName, endType));
	}

	@AuthorizeRole("member")
	@GetMapping("/me/paging")
	public ResponseEntity<ApiResponse<Page<GetPostSimpleResponse>>> getPostsByUserId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUserId Long userId) {
		return ApiResponse.createSuccess(postService.getPostsByUserId(pageable, userId));
	}

	@GetMapping("/{postId}")
	public ResponseEntity<ApiResponse<GetPostResponse>> getPost(@CurrentUser CurrentUserDto currentUser,
		@RequestParam(required = false) String uuid, @PathVariable Long postId) {
		return ApiResponse.createSuccess(postService.getPost(currentUser, uuid, postId));
	}

	@AuthorizeRole("member")
	@GetMapping("/temp")
	ResponseEntity<ApiResponse<GetTempPostResponse>> getTempPost(@CurrentUserId Long userId) {
		return ApiResponse.createSuccess(postService.getTempPost(userId));
	}

	@AuthorizeRole("member")
	@PostMapping
	public ResponseEntity<ApiResponse<CreatePostResponse>> createPost(@CurrentUser CurrentUserDto currentUser,
		@RequestBody CreatePostRequest request, @RequestParam boolean isTemp) {
		String message = isTemp ? "게시물이 임시 저장 되었습니다." : "게시물을 등록 하였습니다. 포인트 +10";
		return ApiResponse.createSuccess(postService.createPost(currentUser, request, isTemp), message);
	}

	@AuthorizeRole("member")
	@PutMapping("/{postId}")
	public ResponseEntity<ApiResponse<Void>> updatePost(@CurrentUser CurrentUserDto currentUser,
		@PathVariable Long postId, @RequestBody UpdatePostRequest request, @RequestParam boolean isTemp) {
		postService.updatePost(currentUser, postId, request, isTemp);
		String message = isTemp ? "게시물이 임시 저장 되었습니다." : "게시물이 저장 되었습니다.";
		return ApiResponse.createSuccess(message);
	}

	@AuthorizeRole("admin")
	@PutMapping("/{postId}/finish")
	public ResponseEntity<ApiResponse<GetPostResponse>> finishEventPost(@PathVariable Long postId) {
		postService.finishEventPost(postId);
		return ApiResponse.createSuccess("이벤트를 종료하였습니다.");
	}

	@PutMapping("/{postId}/recommend")
	public ResponseEntity<ApiResponse<Void>> recommendPost(@CurrentUserId Long userId, @PathVariable Long postId) {
		return ApiResponse.createSuccess(postService.recommendPost(userId, postId));
	}

	@PutMapping("/{postId}/disrecommend")
	public ResponseEntity<ApiResponse<Void>> disrecommendPost(@CurrentUserId Long userId, @PathVariable Long postId) {
		return ApiResponse.createSuccess(postService.disrecommendPost(userId, postId));
	}

	@AuthorizeRole("member")
	@DeleteMapping("/{postId}")
	public ResponseEntity<ApiResponse<Void>> deletePost(@CurrentUser CurrentUserDto currentUser,
		@PathVariable Long postId) {
		postService.deletePost(currentUser, postId);
		return ApiResponse.createSuccess("게시물이 삭제 되었습니다.");
	}

	@AuthorizeRole("member")
	@GetMapping("/{postId}/isAuthorized")
	public ResponseEntity<ApiResponse<Boolean>> isAuthorizedToEdit(@CurrentUser CurrentUserDto currentUser,
		@PathVariable Long postId) {
		return ApiResponse.createSuccess(postService.isAuthorizedToEdit(currentUser, postId));
	}

	@AuthorizeRole("member")
	@DeleteMapping("/temp")
	ResponseEntity<ApiResponse<Void>> deleteTempPost(@CurrentUserId Long userId) {
		postService.deleteTempPost(userId);
		return ApiResponse.createSuccess();
	}

	@AuthorizeRole("admin")
	@GetMapping("/statistic/users/admin")
	ResponseEntity<ApiResponse<List<GetEventPostCountByAdminResponse>>> getEventPostCountByAdmin(
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
		return ApiResponse.createSuccess(postService.getEventPostCountByAdmin(startTime, endTime));
	}
}
