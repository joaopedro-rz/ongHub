package com.onghub.api.dto.response;

public record CampaignItemResponse(
    Long id,
    String itemName,
    String category,
    int quantityNeeded,
    int quantityReceived,
    String unit
) {}
