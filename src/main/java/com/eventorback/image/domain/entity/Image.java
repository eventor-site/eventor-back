package com.eventorback.image.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.eventorback.post.domain.entity.Post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "images")
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long imageId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	@OnDelete(action = OnDeleteAction.CASCADE) // DB 에서 자동 삭제 처리
	private Post post;

	@Column(name = "original_name")
	private String originalName;

	@Column(name = "new_name")
	private String newName;

	@Column(name = "url")
	private String url;

	@Column(name = "size")
	private Long size;

	@Column(name = "is_thumbnail")
	private Boolean isThumbnail;

	@Column(name = "is_pasted")
	private Boolean isPasted;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Builder
	public Image(Post post, String originalName, String newName, String url, Long size, Boolean isThumbnail,
		Boolean isPasted) {
		this.post = post;
		this.originalName = originalName;
		this.newName = newName;
		this.url = url;
		this.size = size;
		this.isThumbnail = isThumbnail;
		this.isPasted = isPasted;
		this.createdAt = LocalDateTime.now();
	}

}
