package com.onghub.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CampaignItemRequest(
    @NotBlank @Size(max = 200) String itemName,
    @Size(max = 120) String category,
    @PositiveOrZero int quantityNeeded,
    @Size(max = 40) String unit
) {}
