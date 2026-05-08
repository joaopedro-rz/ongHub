package com.onghub.api.controller;

import com.onghub.api.dto.request.FinancialDonationRequest;
import com.onghub.api.dto.request.MaterialDonationRequest;
import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.dto.response.DonationResponse;
import com.onghub.api.service.DonationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping("/financial")
    @PreAuthorize("hasAnyRole('DONOR','ADMIN','ONG_MANAGER','VOLUNTEER')")
    public ResponseEntity<ApiResponse<DonationResponse>> createFinancial(
        @Valid @RequestBody FinancialDonationRequest request,
        Principal principal
    ) {
        DonationResponse created = donationService.createFinancial(request, principal.getName());
        return ResponseEntity.status(201).body(ApiResponse.success(created, "Doacao registrada"));
    }

    @PostMapping("/material")
    @PreAuthorize("hasAnyRole('DONOR','ADMIN','ONG_MANAGER','VOLUNTEER')")
    public ResponseEntity<ApiResponse<DonationResponse>> createMaterial(
        @Valid @RequestBody MaterialDonationRequest request,
        Principal principal
    ) {
        DonationResponse created = donationService.createMaterial(request, principal.getName());
        return ResponseEntity.status(201).body(ApiResponse.success(created, "Doacao registrada"));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> mine(Pageable pageable, Principal principal) {
        Page<DonationResponse> page = donationService.listMine(pageable, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(page, "Historico"));
    }

    // Backwards-compatible alias (before Module 4 spec update)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> mineAlias(Pageable pageable, Principal principal) {
        return mine(pageable, principal);
    }

    @GetMapping("/campaign/{campaignId}")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> forCampaign(
        @PathVariable Long campaignId,
        Pageable pageable,
        Principal principal
    ) {
        Page<DonationResponse> page = donationService.listForCampaign(pageable, campaignId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(page, "Doacoes da campanha"));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<DonationResponse>> confirm(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(donationService.confirm(id, principal.getName()), "Doacao confirmada"));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<DonationResponse>> reject(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(donationService.reject(id, principal.getName()), "Doacao rejeitada"));
    }

    @GetMapping(value = "/{id}/receipt", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> receipt(@PathVariable Long id, Principal principal) {
        byte[] pdf = donationService.downloadReceipt(id, principal.getName());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recibo-doacao.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    // Backwards-compatible alias (before Module 4 spec update)
    @GetMapping(value = "/{id}/receipt.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> receiptPdfAlias(@PathVariable Long id, Principal principal) {
        return receipt(id, principal);
    }
}
