package com.onghub.api.dto.response;

import java.util.Set;

public record UserResponse(
    Long id,
    String name,
    String email,
    String phone,
    String profileImageUrl,
    boolean enabled,
    Set<String> roles
) {}
