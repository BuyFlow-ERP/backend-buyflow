package com.buyflow.erp.Dto;

import java.util.List;

public record LoginResponse(
        String accessToken,
        UserResponse user,
        List<String> roles,
        List<String> permissions
) {
}

