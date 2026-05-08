package com.onghub.api.dto.request;

import com.onghub.api.entity.CampaignStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CampaignCreateRequest(
    @NotNull Long ngoId,
    @NotBlank @Size(max = 255) String title,
    String description,
    BigDecimal financialGoal,
    LocalDate startDate,
    LocalDate endDate,
    @Size(max = 512) String coverImageUrl,
    CampaignStatus status,
    boolean urgent,
    @Size(max = 120) String category
) {}
