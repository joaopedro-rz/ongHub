package com.onghub.api.dto.response.report;

import java.math.BigDecimal;

public record TopDonorResponse(
    String donorEmail,
    BigDecimal donationsTotal
) {}

