package com.eventorback.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventorback.post.domain.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

	@Query(value = "SELECT * FROM post WHERE MATCH(title, content) AGAINST (:keyword IN BOOLEAN MODE) ORDER BY created_at DESC", nativeQuery = true)
	List<Post> searchPosts(@Param("keyword") String keyword);

}