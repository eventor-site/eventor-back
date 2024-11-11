package com.eventorback.userrole.domain.entity;

import com.sikyeojoback.role.domain.entity.Role;
import com.sikyeojoback.user.domain.entity.User;

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
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_role_id")
	private Long userId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "role_id")
	private Role role;

	@Builder
	public UserRole(User user, Role role) {
		this.user = user;
		this.role = role;
	}

	public static UserRole toEntity(User user, Role role) {
		return UserRole.builder()
			.user(user)
			.role(role)
			.build();
	}

}
