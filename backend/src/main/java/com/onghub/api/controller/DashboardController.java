package com.onghub.api.controller;

import com.onghub.api.dto.dashboard.DashboardApiDtos;
import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashboardApiDtos.AdminSummary>> admin() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.adminSummary(), "Dashboard admin"));
    }

    @GetMapping("/ngo/{ngoId}")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<DashboardApiDtos.NgoSummary>> ngo(@PathVariable Long ngoId, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.ngoSummary(ngoId, principal.getName()), "Dashboard ONG"));
    }

    @GetMapping("/donor")
    public ResponseEntity<ApiResponse<DashboardApiDtos.DonorSummary>> donor(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.donorSummary(principal.getName()), "Dashboard doador"));
    }
}
