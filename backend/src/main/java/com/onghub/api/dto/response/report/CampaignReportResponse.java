package com.onghub.api.dto.response.report;

import com.onghub.api.dto.response.DonationResponse;

import java.util.List;

public record CampaignReportResponse(
    Long campaignId,
    String campaignTitle,
    List<DonationResponse> donations
) {}

