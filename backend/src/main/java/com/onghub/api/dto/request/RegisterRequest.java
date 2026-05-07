package com.onghub.api.dto.request;

import com.onghub.api.entity.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(max = 120) String name,
    @Email @NotBlank String email,
    @NotBlank @Size(min = 6) String password,
    @NotNull RoleName role
) {}
