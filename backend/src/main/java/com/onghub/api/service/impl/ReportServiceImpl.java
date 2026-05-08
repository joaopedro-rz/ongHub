package com.onghub.api.service.impl;

import com.onghub.api.dto.dashboard.DashboardApiDtos;
import com.onghub.api.dto.response.DonationResponse;
import com.onghub.api.dto.response.report.AdminDashboardReportResponse;
import com.onghub.api.dto.response.report.CampaignReportResponse;
import com.onghub.api.dto.response.report.DonorDashboardReportResponse;
import com.onghub.api.dto.response.report.NgoDashboardReportResponse;
import com.onghub.api.dto.response.report.MonthlyGrowthResponse;
import com.onghub.api.dto.response.report.TopDonorResponse;
import com.onghub.api.entity.*;
import com.onghub.api.repository.DonationReceiptRepository;
import com.onghub.api.repository.DonationRepository;
import com.onghub.api.service.DashboardService;
import com.onghub.api.service.ReportService;
import com.onghub.api.service.TransparencyReportService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final DashboardService dashboardService;
    private final TransparencyReportService transparencyReportService;
    private final DonationRepository donationRepository;
    private final DonationReceiptRepository donationReceiptRepository;

    public ReportServiceImpl(
        DashboardService dashboardService,
        TransparencyReportService transparencyReportService,
        DonationRepository donationRepository,
        DonationReceiptRepository donationReceiptRepository
    ) {
        this.dashboardService = dashboardService;
        this.transparencyReportService = transparencyReportService;
        this.donationRepository = donationRepository;
        this.donationReceiptRepository = donationReceiptRepository;
    }

    @Override
    public AdminDashboardReportResponse getAdminDashboard() {
        DashboardApiDtos.AdminSummary base = dashboardService.adminSummary();
        // Monthly growth wasn't present in the existing dashboard DTOs; keep it as an empty list for now.
        List<MonthlyGrowthResponse> monthlyGrowth = List.of();
        return new AdminDashboardReportResponse(
            base.ngos(),
            base.users(),
            base.campaigns(),
            base.donations(),
            base.volunteerApplications(),
            monthlyGrowth
        );
    }

    @Override
    public NgoDashboardReportResponse getNgoDashboard(Long ngoId) {
        String principalEmail = currentPrincipalEmail();
        DashboardApiDtos.NgoSummary base = dashboardService.ngoSummary(ngoId, principalEmail);
        // Top donors + campaign progress aren't part of the existing dashboard DTOs.
        List<TopDonorResponse> topDonors = List.of();
        List<Object> campaignProgress = List.of();
        return new NgoDashboardReportResponse(
            base.donationsCount(),
            base.activeCampaigns(),
            base.approvedVolunteers(),
            base.confirmedFinancialTotal(),
            topDonors,
            campaignProgress
        );
    }

    @Override
    public DonorDashboardReportResponse getDonorDashboard(String userId) {
        DashboardApiDtos.DonorSummary base = dashboardService.donorSummary(userId);
        return new DonorDashboardReportResponse(
            base.donationsCount(),
            base.confirmedFinancialTotal(),
            base.supportedNgos(),
            List.of()
        );
    }

    @Override
    public CampaignReportResponse getCampaignReport(Long campaignId) {
        List<Donation> donations = donationRepository.findByCampaign_IdOrderByCreatedAtDesc(campaignId);
        String campaignTitle = donations.isEmpty()
            ? null
            : donations.get(0).getCampaign().getTitle();

        List<DonationResponse> rows = donations.stream()
            .map(this::toDonationResponse)
            .toList();

        return new CampaignReportResponse(campaignId, campaignTitle, rows);
    }

    @Override
    public byte[] exportNgoReportCsv(Long ngoId) {
        String principalEmail = currentPrincipalEmail();
        return transparencyReportService.csv(ngoId, principalEmail);
    }

    @Override
    public byte[] exportNgoReportPdf(Long ngoId) {
        String principalEmail = currentPrincipalEmail();
        return transparencyReportService.pdf(ngoId, principalEmail);
    }

    private DonationResponse toDonationResponse(Donation d) {
        String receiptNumber = null;
        if (d.getStatus() == DonationStatus.CONFIRMED) {
            receiptNumber = donationReceiptRepository.findByDonation_Id(d.getId())
                .map(DonationReceipt::getReceiptNumber)
                .orElse(null);
        }

        Long itemId = d.getCampaignItem() != null ? d.getCampaignItem().getId() : null;

        return new DonationResponse(
            d.getId(),
            d.getDonationType(),
            d.getStatus(),
            d.getCampaign().getId(),
            d.getCampaign().getTitle(),
            d.getAmount(),
            d.getPaymentMethod(),
            d.getProofUrl(),
            d.getMaterialDescription(),
            d.getQuantity(),
            itemId,
            d.getNotes(),
            d.getConfirmedAt(),
            d.getCreatedAt(),
            receiptNumber
        );
    }

    private String currentPrincipalEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return null;
        }
        return auth.getName();
    }
}

