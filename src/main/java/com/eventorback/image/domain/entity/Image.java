package com.eventorback.image.domain.entity;

import java.time.LocalDateTime;

import com.eventorback.post.domain.entity.Post;

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
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long imageId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	private Post post;

	@Column(name = "original_name")
	private String originalName;

	@Column(name = "new_name")
	private String newName;

	@Column(name = "url")
	private String url;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Builder
	public Image(Post post, String originalName, String newName, String url) {
		this.post = post;
		this.originalName = originalName;
		this.newName = newName;
		this.url = url;
		this.createdAt = LocalDateTime.now();
	}

}
