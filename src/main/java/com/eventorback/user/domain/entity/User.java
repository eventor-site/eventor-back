package com.eventorback.user.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.eventorback.grade.domain.entity.Grade;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;

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
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "status_id")
	private Status status;

	@ManyToOne(optional = false)
	@JoinColumn(name = "grade_id")
	private Grade grade;

	@Column(name = "identifier")
	private String identifier;

	@Column(name = "password")
	private String password;

	@Column(name = "name")
	private String name;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "email")
	private String email;

	@Column(name = "birth")
	private LocalDate birth;

	@Column(name = "gender")
	private String gender;

	@Column(name = "phone")
	private String phone;

	@Column(name = "oauth_id")
	private String oauthId;

	@Column(name = "point")
	private Long point;

	@Column(name = "updated_time")
	private LocalDateTime updatedTime;

	@Column(name = "last_login_time")
	private LocalDateTime lastLoginTime;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Builder
	public User(Status status, Grade grade, String identifier, String password, String name, String nickname,
		String email, LocalDate birth, String gender, String phone, String oauthId) {
		this.status = status;
		this.grade = grade;
		this.identifier = identifier;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.birth = birth;
		this.gender = gender;
		this.phone = phone;
		this.point = 0L;
		this.createdAt = LocalDateTime.now();
		this.oauthId = oauthId;
	}

	public static User toEntity(Status status, Grade grade, SignUpRequest request, String encodedPassword) {
		return User.builder()
			.status(status)
			.grade(grade)
			.identifier(request.identifier())
			.password(encodedPassword)
			.name(request.name())
			.nickname(request.nickname())
			.email(request.email())
			.birth(toLocalDate(request.birth()))
			.gender(request.gender())
			.phone(request.phone())
			.oauthId(request.oauthId())
			.build();
	}

	public static LocalDate toLocalDate(String birth) {
		if (birth == null) {
			return null;
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			return LocalDate.parse(birth, formatter);
		}
	}

	public void updateUser(UpdateUserRequest request) {
		this.name = request.name();
		this.nickname = request.nickname();
		this.email = request.email();
		this.phone = request.phone();
		this.birth = request.birth();
		this.gender = request.gender();
		this.updatedTime = LocalDateTime.now();
	}

	public void updateLastLoginTime(UpdateLastLoginTimeRequest request) {
		this.lastLoginTime = request.lastLoginTime();
	}

	public void modifyPassword(String encryptedNewPassword) {
		this.password = encryptedNewPassword;
	}

	public void updateStatus(Status status) {
		this.status = status;
	}

	public void oauth2Connection(String oauthId) {
		this.oauthId = oauthId;
	}
}
