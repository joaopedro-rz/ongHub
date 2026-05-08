package com.onghub.api.dto.response.report;

import java.math.BigDecimal;

public record MonthlyGrowthResponse(
    String month,
    long donationsCount,
    BigDecimal donationsTotal
) {}

