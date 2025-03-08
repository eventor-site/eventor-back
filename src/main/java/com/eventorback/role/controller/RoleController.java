package com.eventorback.role.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.role.domain.dto.RoleDto;
import com.eventorback.role.service.RoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/roles")
public class RoleController {
	private final RoleService roleService;

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ApiResponse<Page<RoleDto>> getRoles(@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(roleService.getRoles(pageable));
	}

	@GetMapping("/{roleId}")
	public ApiResponse<RoleDto> getRole(@PathVariable Long roleId) {
		return ApiResponse.createSuccess(roleService.getRole(roleId));
	}

	@PostMapping
	public ApiResponse<Void> createRole(
		@RequestBody RoleDto request) {
		roleService.createRole(request);
		return ApiResponse.createSuccess("권한이 생성 되었습니다.");
	}

	@PutMapping("/{roleId}")
	public ApiResponse<Void> updateRole(@PathVariable Long roleId,
		@RequestBody RoleDto request) {
		roleService.updateRole(roleId, request);
		return ApiResponse.createSuccess("권한이 수정 되었습니다.");
	}

	@DeleteMapping("/{roleId}")
	public ApiResponse<Void> deleteRole(@PathVariable Long roleId) {
		roleService.deleteRole(roleId);
		return ApiResponse.createSuccess("권한이 삭제 되었습니다.");
	}

}
