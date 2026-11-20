package com.eventorback.category.domain.dto.response;

import java.util.List;

/**
 * 카테고리 트리(메뉴용). 같은 부모 안에서는 group_order 순.
 */
public record CategoryTreeNode(Long categoryId, String name, int depth, List<CategoryTreeNode> children) {
}
