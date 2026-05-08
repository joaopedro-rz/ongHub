package com.onghub.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MaterialDonationRequest(
    @NotNull Long campaignId,
    @NotBlank @Size(max = 500) String materialDescription,
    @Positive int quantity,
    Long campaignItemId,
    @Size(max = 512) String proofUrl,
    String notes
) {}
