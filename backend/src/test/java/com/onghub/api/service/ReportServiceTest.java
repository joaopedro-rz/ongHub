package com.onghub.api.service;

import com.onghub.api.dto.dashboard.DashboardApiDtos;
import com.onghub.api.dto.response.DonationResponse;
import com.onghub.api.dto.response.report.AdminDashboardReportResponse;
import com.onghub.api.dto.response.report.CampaignReportResponse;
import com.onghub.api.dto.response.report.DonorDashboardReportResponse;
import com.onghub.api.dto.response.report.NgoDashboardReportResponse;
import com.onghub.api.entity.*;
import com.onghub.api.repository.DonationReceiptRepository;
import com.onghub.api.repository.DonationRepository;
import com.onghub.api.service.impl.ReportServiceImpl;
import com.onghub.api.service.TransparencyReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private DashboardService dashboardService;
    private TransparencyReportService transparencyReportService;
    private DonationRepository donationRepository;
    private DonationReceiptRepository donationReceiptRepository;

    private ReportService reportService;

    @BeforeEach
    void setup() {
        dashboardService = mock(DashboardService.class);
        transparencyReportService = mock(TransparencyReportService.class);
        donationRepository = mock(DonationRepository.class);
        donationReceiptRepository = mock(DonationReceiptRepository.class);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "manager@test.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ONG_MANAGER"))
            )
        );

        reportService = new ReportServiceImpl(
            dashboardService,
            transparencyReportService,
            donationRepository,
            donationReceiptRepository
        );
    }

    @Test
    void getAdminDashboardMapsBaseValues() {
        when(dashboardService.adminSummary()).thenReturn(
            new DashboardApiDtos.AdminSummary(1, 2, 3, 4, 5)
        );

        AdminDashboardReportResponse resp = reportService.getAdminDashboard();

        assertThat(resp.ngos()).isEqualTo(1);
        assertThat(resp.users()).isEqualTo(2);
        assertThat(resp.campaigns()).isEqualTo(3);
        assertThat(resp.donations()).isEqualTo(4);
        assertThat(resp.volunteerApplications()).isEqualTo(5);
        assertThat(resp.monthlyGrowth()).isEmpty();
    }

    @Test
    void getNgoDashboardUsesPrincipalEmail() {
        when(dashboardService.ngoSummary(eq(10L), eq("manager@test.com"))).thenReturn(
            new DashboardApiDtos.NgoSummary(
                7,
                2,
                1,
                new BigDecimal("123.45")
            )
        );

        NgoDashboardReportResponse resp = reportService.getNgoDashboard(10L);
        assertThat(resp.donationsCount()).isEqualTo(7);
        assertThat(resp.activeCampaigns()).isEqualTo(2);
        assertThat(resp.approvedVolunteers()).isEqualTo(1);
        assertThat(resp.confirmedFinancialTotal()).isEqualByComparingTo("123.45");
        assertThat(resp.topDonors()).isEmpty();
    }

    @Test
    void exportNgoReportCsvDelegates() {
        when(transparencyReportService.csv(10L, "manager@test.com")).thenReturn(new byte[] { 9, 8, 7 });

        byte[] out = reportService.exportNgoReportCsv(10L);
        assertThat(out).containsExactly(9, 8, 7);
    }

    @Test
    void getCampaignReportMapsDonationRows() {
        Campaign campaign = new Campaign();
        campaign.setId(99L);
        campaign.setTitle("Camp X");

        Donation donation = new Donation();
        donation.setId(1L);
        donation.setDonationType(DonationType.FINANCIAL);
        donation.setStatus(DonationStatus.CONFIRMED);
        donation.setCampaign(campaign);
        donation.setAmount(new BigDecimal("50.00"));
        donation.setPaymentMethod("pix");
        donation.setProofUrl("proof");
        donation.setNotes("notes");
        donation.setConfirmedAt(LocalDateTime.now());
        donation.setCreatedAt(LocalDateTime.now());

        when(donationRepository.findByCampaign_IdOrderByCreatedAtDesc(99L)).thenReturn(List.of(donation));

        DonationReceipt receipt = new DonationReceipt();
        receipt.setDonation(donation);
        receipt.setReceiptNumber("REC-1");
        when(donationReceiptRepository.findByDonation_Id(1L)).thenReturn(Optional.of(receipt));

        CampaignReportResponse resp = reportService.getCampaignReport(99L);

        assertThat(resp.campaignId()).isEqualTo(99L);
        assertThat(resp.campaignTitle()).isEqualTo("Camp X");
        assertThat(resp.donations()).hasSize(1);
        assertThat(resp.donations().get(0).receiptNumber()).isEqualTo("REC-1");
    }
}

