package com.buyflow.erp.Dto;

import java.util.List;

/**
 * 역할-권한 저장 요청 바디.
 * 프론트엔드 권한 관리 화면에서 체크된 권한 코드 목록을 보낸다.
 * 예: { "permissionCodes": ["dashboard.read", "products.read", ...] }
 */
public record RolePermissionUpdateRequest(
        List<String> permissionCodes
) {
}