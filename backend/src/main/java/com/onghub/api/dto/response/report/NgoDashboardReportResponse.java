package com.onghub.api.dto.response.report;

import com.onghub.api.entity.DonationStatus;

import java.math.BigDecimal;
import java.util.List;

public record NgoDashboardReportResponse(
    long donationsCount,
    long activeCampaigns,
    long approvedVolunteers,
    BigDecimal confirmedFinancialTotal,
    List<TopDonorResponse> topDonors,
    List<Object> campaignProgress
) {}

