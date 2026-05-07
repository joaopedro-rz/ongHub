package com.onghub.api.dto.response;

import com.onghub.api.entity.NgoStatus;

public record NgoSummaryResponse(
    Long id,
    String name,
    NgoStatus status,
    Long categoryId,
    String city,
    String state
) {
}
