package com.onghub.api.service;

import com.onghub.api.dto.response.report.AdminDashboardReportResponse;
import com.onghub.api.dto.response.report.CampaignReportResponse;
import com.onghub.api.dto.response.report.DonorDashboardReportResponse;
import com.onghub.api.dto.response.report.NgoDashboardReportResponse;

public interface ReportService {

    AdminDashboardReportResponse getAdminDashboard();

    NgoDashboardReportResponse getNgoDashboard(Long ngoId);

    DonorDashboardReportResponse getDonorDashboard(String userId);

    CampaignReportResponse getCampaignReport(Long campaignId);

    byte[] exportNgoReportCsv(Long ngoId);

    byte[] exportNgoReportPdf(Long ngoId);
}

