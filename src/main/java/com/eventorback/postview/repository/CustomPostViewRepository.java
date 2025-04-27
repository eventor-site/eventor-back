package com.eventorback.postview.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.post.domain.dto.response.GetMainPostResponse;

public interface CustomPostViewRepository {

	Page<GetMainPostResponse> getPostViewsByViewerId(String viewerId, Pageable pageable);
}
