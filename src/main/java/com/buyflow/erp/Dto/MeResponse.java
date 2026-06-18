package com.buyflow.erp.Dto;

import java.util.List;

public record MeResponse(
        UserResponse user,
        List<String> roles,
        List<String> permissions
) {
}

