package com.eventorback.role.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sikyeojoback.role.domain.dto.RoleDto;
import com.sikyeojoback.role.service.RoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/roles")
public class RoleController {
	private final RoleService roleService;

	@GetMapping
	public ResponseEntity<List<RoleDto>> getRoles() {
		return ResponseEntity.status(HttpStatus.OK).body(roleService.getRoles());
	}

	@GetMapping("/{roleId}")
	public ResponseEntity<RoleDto> getRole(@PathVariable Long roleId) {
		return ResponseEntity.status(HttpStatus.OK).body(roleService.getRole(roleId));
	}

	@PostMapping
	public ResponseEntity<Void> createRole(
		@RequestBody RoleDto request) {
		roleService.createRole(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{roleId}")
	public ResponseEntity<Void> updateRole(@PathVariable Long roleId,
		@RequestBody RoleDto request) {
		roleService.updateRole(roleId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{roleId}")
	public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
		roleService.deleteRole(roleId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
