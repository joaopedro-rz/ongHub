package com.onghub.api.dto.response.report;

import java.util.List;

public record AdminDashboardReportResponse(
    long ngos,
    long users,
    long campaigns,
    long donations,
    long volunteerApplications,
    List<MonthlyGrowthResponse> monthlyGrowth
) {}

