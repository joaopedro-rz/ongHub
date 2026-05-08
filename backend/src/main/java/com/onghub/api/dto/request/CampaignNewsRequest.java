package com.onghub.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CampaignNewsRequest(
    @Size(max = 255) String title,
    @NotBlank String body
) {}
