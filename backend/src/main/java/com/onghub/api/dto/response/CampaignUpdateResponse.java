package com.onghub.api.dto.response;

import java.time.LocalDateTime;

public record CampaignUpdateResponse(
    Long id,
    String title,
    String body,
    LocalDateTime createdAt
) {}
