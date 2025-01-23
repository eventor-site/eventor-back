package com.eventorback.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.comment.domain.entity.CommentClosure;

public interface CommentClosureRepository extends JpaRepository<CommentClosure, Long> {

	List<CommentClosure> findByDescendant(Comment descendant);
}
