package com.onghub.api.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CampaignItemPatchRequest(
    @Size(max = 200) String itemName,
    @Size(max = 120) String category,
    @PositiveOrZero Integer quantityNeeded,
    @PositiveOrZero Integer quantityReceived,
    @Size(max = 40) String unit
) {}
