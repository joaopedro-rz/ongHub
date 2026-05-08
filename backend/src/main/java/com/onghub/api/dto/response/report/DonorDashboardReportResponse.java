package com.onghub.api.dto.response.report;

import java.math.BigDecimal;
import java.util.List;

import com.onghub.api.dto.response.DonationResponse;

public record DonorDashboardReportResponse(
    long donationsCount,
    BigDecimal confirmedFinancialTotal,
    long supportedNgos,
    List<DonationResponse> donationHistory
) {}

