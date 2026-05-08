package com.onghub.api.dto.response;

import com.onghub.api.entity.DonationStatus;
import com.onghub.api.entity.DonationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DonationSummaryResponse(
    Long id,
    DonationType donationType,
    DonationStatus status,
    Long campaignId,
    String campaignTitle,
    BigDecimal amount,
    String materialDescription,
    Integer quantity,
    LocalDateTime confirmedAt,
    LocalDateTime createdAt,
    String receiptNumber
) {}

