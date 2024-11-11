package com.eventorback.role.domain.entity;

import com.eventorback.role.domain.dto.RoleDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Long roleId;

	@Column(name = "name")
	private String name;

	@Builder
	public Role(String name) {
		this.name = name;
	}

	public static Role toEntity(RoleDto request) {
		return Role.builder()
			.name(request.name())
			.build();
	}

	public void updateRoleName(String name) {
		this.name = name;
	}

}
