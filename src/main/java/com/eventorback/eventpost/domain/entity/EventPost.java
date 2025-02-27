package com.eventorback.eventpost.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
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
@Table(name = "event_posts")
public class EventPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_post_id")
	private Long eventPostId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	@OnDelete(action = OnDeleteAction.CASCADE) // DB 에서 자동 삭제 처리
	private Post post;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Column(name = "link")
	private String link;

	@Builder
	public EventPost(Post post, LocalDateTime startTime, LocalDateTime endTime, String link) {
		this.post = post;
		this.startTime = startTime;
		this.endTime = endTime;
		this.link = link;
	}

	public static EventPost toEntity(Post post, CreatePostRequest request) {
		return EventPost.builder()
			.post(post)
			.startTime(request.startTime())
			.endTime(request.endTime())
			.link(request.link())
			.build();
	}

	public void update(UpdatePostRequest request) {
		this.startTime = request.startTime();
		this.endTime = request.endTime();
		this.link = request.link();
	}
}
