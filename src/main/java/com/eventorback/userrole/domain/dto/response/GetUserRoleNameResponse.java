package com.eventorback.userrole.domain.dto.response;

import java.util.List;

public record GetUserRoleNameResponse(
	List<String> roleNameList) {
}
