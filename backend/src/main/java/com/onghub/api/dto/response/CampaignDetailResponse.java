package com.onghub.api.dto.response;

import com.onghub.api.entity.CampaignStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CampaignDetailResponse(
    Long id,
    String title,
    String description,
    BigDecimal financialGoal,
    LocalDate startDate,
    LocalDate endDate,
    String coverImageUrl,
    CampaignStatus status,
    boolean urgent,
    String category,
    Long ngoId,
    String ngoName,
    List<CampaignItemResponse> items,
    List<CampaignUpdateResponse> updates
) {}
