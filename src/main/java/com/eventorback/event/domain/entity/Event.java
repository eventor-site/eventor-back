package com.eventorback.event.domain.entity;

import java.time.LocalDateTime;

import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_id")
	private Long eventPostId;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Column(name = "link", length = 1000)
	private String link;

	@Column(name = "endType")
	private String endType;

	@Builder
	public Event(LocalDateTime startTime, LocalDateTime endTime, String link, String endType) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.link = link;
		this.endType = endType;
	}

	public static Event toEntity(CreatePostRequest request) {
		return Event.builder()
			.startTime(request.startTime())
			.endTime(request.endTime())
			.link(request.link())
			.endType(request.endType())
			.build();
	}

	public void update(UpdatePostRequest request) {
		this.startTime = request.startTime();
		this.endTime = request.endTime();
		this.link = request.link();
		this.endType = request.endType();
	}

	public void finishEvent() {
		this.endTime = LocalDateTime.now();
	}
}
