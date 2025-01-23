package com.eventorback.comment.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class CommentClosure {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_closure_id")
	private Long commentClosureId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ancestor_id")
	private Comment ancestor;

	@ManyToOne(optional = false)
	@JoinColumn(name = "descendant_id")
	private Comment descendant;

	@Column(name = "depth")
	private Long depth;

	@Builder
	public CommentClosure(Comment ancestor, Comment descendant, Long depth) {
		this.ancestor = ancestor;
		this.descendant = descendant;
		this.depth = depth;
	}
}
