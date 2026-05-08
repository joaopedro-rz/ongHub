package com.onghub.api.dto.response;

import java.time.LocalDateTime;

public record ReceiptResponse(
    Long donationId,
    String receiptNumber,
    LocalDateTime issuedAt
) {}

