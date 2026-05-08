package com.onghub.api.dto.response;

import com.onghub.api.entity.CampaignStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CampaignSummaryResponse(
    Long id,
    String title,
    CampaignStatus status,
    Long ngoId,
    String ngoName,
    String category,
    boolean urgent,
    String coverImageUrl,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal financialGoal
) {}
