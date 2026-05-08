package com.onghub.api.service;

import com.onghub.api.dto.dashboard.DashboardApiDtos;

public interface DashboardService {

    DashboardApiDtos.AdminSummary adminSummary();

    DashboardApiDtos.NgoSummary ngoSummary(Long ngoId, String principalEmail);

    DashboardApiDtos.DonorSummary donorSummary(String donorEmail);
}
