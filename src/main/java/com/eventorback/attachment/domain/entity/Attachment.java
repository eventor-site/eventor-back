// package com.eventorback.attachment.domain.entity;
//
// import com.eventorback.image.domain.entity.Image;
// import com.eventorback.post.domain.entity.Post;
//
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
//
// @Getter
// @Entity
// @NoArgsConstructor
// public class Attachment {
//
// 	@Id
// 	@GeneratedValue(strategy = GenerationType.IDENTITY)
// 	@Column(name = "attachment_id")
// 	private Long attachmentId;
//
// 	@ManyToOne(optional = false)
// 	@JoinColumn(name = "image_id")
// 	private Image image;
//
// 	@ManyToOne(optional = false)
// 	@JoinColumn(name = "post_id")
// 	private Post post;
//
// 	@Builder
// 	public Attachment(Image image, Post post) {
// 		this.image = image;
// 		this.post = post;
// 	}
//
// 	public static Attachment toEntity(Image image, Post post) {
// 		return Attachment.builder()
// 			.image(image)
// 			.post(post)
// 			.build();
// 	}
//
// }
