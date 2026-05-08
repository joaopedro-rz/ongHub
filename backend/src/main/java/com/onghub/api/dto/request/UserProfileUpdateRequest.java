package com.onghub.api.dto.request;

import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequest(
    @Size(max = 120) String name,
    @Size(max = 30) String phone,
    @Size(max = 512) String profileImageUrl
) {}
