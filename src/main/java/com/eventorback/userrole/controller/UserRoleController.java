package com.eventorback.userrole.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.role.domain.dto.RoleDto;
import com.eventorback.userrole.service.UserRoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/users")
public class UserRoleController {
	private final UserRoleService userRoleService;

	@AuthorizeRole("admin")
	@GetMapping("/{userId}/roles")
	ResponseEntity<List<RoleDto>> getUserRoles(@PathVariable Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(userRoleService.getUserRoles(userId));
	}

	@AuthorizeRole("admin")
	@GetMapping("/{userId}/roles/unassigned")
	ResponseEntity<List<RoleDto>> getUnassignedUserRoles(@PathVariable Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(userRoleService.getUnassignedUserRoles(userId));
	}

	@AuthorizeRole("admin")
	@PostMapping("/{userId}/roles")
	ResponseEntity<Void> createUserRole(@PathVariable Long userId, @RequestParam Long roleId) {
		userRoleService.createUserRole(userId, roleId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@AuthorizeRole("admin")
	@DeleteMapping("/{userId}/roles/{roleId}")
	ResponseEntity<Void> deleteUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
		userRoleService.deleteUserRole(userId, roleId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
