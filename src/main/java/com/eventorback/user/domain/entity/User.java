package com.eventorback.user.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.eventorback.grade.domain.entity.Grade;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLoginAtRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.userrole.domain.entity.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
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

	@Column(name = "identifier", length = 30)
	private String identifier;

	@Column(name = "password", length = 255)
	private String password;

	@Column(name = "name", length = 30)
	private String name;

	@Column(name = "nickname", length = 30)
	private String nickname;

	@Column(name = "email", length = 30)
	private String email;

	@Column(name = "birth")
	private LocalDate birth;

	@Column(name = "gender", length = 3)
	private String gender;

	@Column(name = "phone", length = 15)
	private String phone;

	@Column(name = "point")
	private Long point;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "login_at")
	private LocalDateTime loginAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "nickname_changed_at")
	private LocalDateTime nicknameChangedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "oauth_id")
	private String oauthId;

	@Column(name = "oauth_type")
	private String oauthType;

	@OneToMany(mappedBy = "user")
	private List<UserRole> userRoles;

	@Builder
	public User(Status status, Grade grade, String identifier, String password, String name, String nickname,
		String email, LocalDate birth, String gender, String phone, String oauthId, String oauthType) {
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
		this.oauthType = oauthType;
	}

	public static User toEntity(Status status, Grade grade, SignUpRequest request, String encodedPassword) {
		return User.builder()
			.status(status)
			.grade(grade)
			.identifier(request.identifier())
			.password(encodedPassword)
			.name(request.name())
			.nickname(request.nickname())
			.email(request.identifier() != null ? request.identifier() : request.email())
			.birth(toLocalDate(request.birth()))
			.gender(request.gender())
			.phone(request.phone())
			.oauthId(request.oauthId())
			.oauthType(request.oauthType())
			.build();
	}

	public static LocalDate toLocalDate(String birth) {
		if (birth == null || birth.isEmpty()) {
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
		this.updatedAt = LocalDateTime.now();
	}

	public void updateLoginAt(UpdateLoginAtRequest request) {
		this.loginAt = request.loginAt();
	}

	public void updateNicknameChangedAt() {
		this.nicknameChangedAt = LocalDateTime.now();
	}

	public void modifyPassword(String encryptedNewPassword) {
		this.password = encryptedNewPassword;
	}

	public void updateStatus(Status status) {
		this.status = status;
	}

	public void updatePoint(Long point) {
		this.point += point;

		if (this.point < 0) {
			this.point = 0L;
		}
	}

	public void updateAttribute(Status status, Grade grade) {
		this.status = status;
		this.grade = grade;
	}

	public void updateGrade(Grade grade) {
		this.grade = grade;
	}

	public void updateUpdatedAt() {
		this.updatedAt = LocalDateTime.now();
	}

	public void withdrawUser() {
		this.nickname = "[탈퇴]";
		this.email = "[탈퇴]";
		this.deletedAt = LocalDateTime.now();

		if (this.identifier != null) {
			this.identifier = "[탈퇴]";
		} else {
			this.oauthId = "[탈퇴]";
		}

	}
}
