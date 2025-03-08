package com.eventorback.userrole.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.global.dto.ApiResponse;
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
	ResponseEntity<ApiResponse<List<RoleDto>>> getUserRoles(@PathVariable Long userId) {
		return ApiResponse.createSuccess(userRoleService.getUserRoles(userId));
	}

	@AuthorizeRole("admin")
	@GetMapping("/{userId}/roles/unassigned")
	ResponseEntity<ApiResponse<List<RoleDto>>> getUnassignedUserRoles(@PathVariable Long userId) {
		return ApiResponse.createSuccess(userRoleService.getUnassignedUserRoles(userId));
	}

	@AuthorizeRole("admin")
	@PostMapping("/{userId}/roles")
	ResponseEntity<ApiResponse<Void>> createUserRole(@PathVariable Long userId, @RequestParam Long roleId) {
		userRoleService.createUserRole(userId, roleId);
		return ApiResponse.createSuccess("회원 권한이 추가 되었습니다.");
	}

	@AuthorizeRole("admin")
	@DeleteMapping("/{userId}/roles/{roleId}")
	ResponseEntity<ApiResponse<Void>> deleteUserRole(@PathVariable Long userId, @PathVariable Long roleId) {
		userRoleService.deleteUserRole(userId, roleId);
		return ApiResponse.createSuccess("회원 권한이 삭제 되었습니다.");
	}

}
