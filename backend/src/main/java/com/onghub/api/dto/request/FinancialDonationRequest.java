package com.onghub.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FinancialDonationRequest(
    @NotNull Long campaignId,
    @NotNull @Positive BigDecimal amount,
    @Size(max = 60) String paymentMethod,
    @Size(max = 512) String proofUrl,
    String notes
) {}
