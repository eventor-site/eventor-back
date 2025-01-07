package com.eventorback.user.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.eventorback.status.domain.entity.Status;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.usergrade.domain.entity.UserGrade;

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
	@JoinColumn(name = "user_grade_id")
	private UserGrade userGrade;

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

	@Column(name = "sso_id")
	private String ssoId;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_time")
	private LocalDateTime updatedTime;

	@Column(name = "last_login_time")
	private LocalDateTime lastLoginTime;

	@Builder
	public User(Status status, UserGrade userGrade, String identifier, String password, String name, String nickname,
		String email, LocalDate birth, String gender, String phone, LocalDateTime createdAt) {
		this.status = status;
		this.userGrade = userGrade;
		this.identifier = identifier;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.birth = birth;
		this.gender = gender;
		this.phone = phone;
		this.createdAt = createdAt;
	}

	public static User toEntity(Status status, UserGrade userGrade, SignUpRequest request, String encodedPassword) {
		return User.builder()
			.status(status)
			.userGrade(userGrade)
			.identifier(request.identifier())
			.password(encodedPassword)
			.name(request.name())
			.nickname(request.nickName())
			.email(request.email())
			.birth(toLocalDate(request.birth()))
			.gender(request.gender())
			.phone(request.phone())
			.createdAt(LocalDateTime.now())
			.build();
	}

	public static LocalDate toLocalDate(String birth) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return LocalDate.parse(birth, formatter);
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

	public void withdrawUser(Status status) {
		this.status = status;
	}
}
