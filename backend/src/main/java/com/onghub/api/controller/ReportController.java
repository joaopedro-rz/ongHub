package com.onghub.api.controller;

import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.dto.response.report.AdminDashboardReportResponse;
import com.onghub.api.dto.response.report.CampaignReportResponse;
import com.onghub.api.dto.response.report.DonorDashboardReportResponse;
import com.onghub.api.dto.response.report.NgoDashboardReportResponse;
import com.onghub.api.exception.BadRequestException;
import com.onghub.api.service.ReportService;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminDashboardReportResponse>> adminDashboard() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getAdminDashboard(), "Dashboard admin"));
    }

    @GetMapping("/ngo/{ngoId}/dashboard")
    @PreAuthorize("hasAnyRole('ONG_MANAGER','ADMIN')")
    public ResponseEntity<ApiResponse<NgoDashboardReportResponse>> ngoDashboard(@PathVariable Long ngoId) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getNgoDashboard(ngoId), "Dashboard ONG"));
    }

    @GetMapping("/donor/dashboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DonorDashboardReportResponse>> donorDashboard(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getDonorDashboard(principal.getName()), "Dashboard doador"));
    }

    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<ApiResponse<CampaignReportResponse>> campaignReport(@PathVariable Long campaignId) {
        return ResponseEntity.ok(ApiResponse.success(reportService.getCampaignReport(campaignId), "Relatorio campanha"));
    }

    @GetMapping("/ngo/{ngoId}/export")
    @PreAuthorize("hasAnyRole('ONG_MANAGER','ADMIN')")
    public ResponseEntity<byte[]> ngoExport(
        @PathVariable Long ngoId,
        @RequestParam(name = "format", defaultValue = "csv") String format
    ) {
        if ("csv".equalsIgnoreCase(format)) {
            byte[] csv = reportService.exportNgoReportCsv(ngoId);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transparency.csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(csv);
        }
        if ("pdf".equalsIgnoreCase(format)) {
            byte[] pdf = reportService.exportNgoReportPdf(ngoId);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transparency.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
        }
        throw new BadRequestException("format deve ser 'csv' ou 'pdf'");
    }

    // Backwards-compatible transparency exports used by the existing frontend
    @GetMapping(value = "/ngos/{ngoId}/transparency.csv", produces = "text/csv;charset=UTF-8")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<byte[]> transparencyCsv(@PathVariable Long ngoId) {
        byte[] csv = reportService.exportNgoReportCsv(ngoId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transparency.csv")
            .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
            .body(csv);
    }

    @GetMapping(value = "/ngos/{ngoId}/transparency.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<byte[]> transparencyPdf(@PathVariable Long ngoId) {
        byte[] pdf = reportService.exportNgoReportPdf(ngoId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transparency.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
