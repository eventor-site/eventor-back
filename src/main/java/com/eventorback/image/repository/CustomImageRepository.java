package com.eventorback.image.repository;

import java.util.List;

import com.eventorback.image.domain.dto.response.GetImageResponse;

public interface CustomImageRepository {

	List<GetImageResponse> getAllByPostId(Long postId);

	Long sumSizeByPostPostId(Long postId);
}
