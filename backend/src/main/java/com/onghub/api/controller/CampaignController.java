package com.onghub.api.controller;

import com.onghub.api.dto.request.*;
import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.dto.response.CampaignDetailResponse;
import com.onghub.api.dto.response.CampaignSummaryResponse;
import com.onghub.api.entity.CampaignStatus;
import com.onghub.api.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Page<CampaignSummaryResponse>>> listPublic(
        Pageable pageable,
        @RequestParam(required = false) Long ngoId,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Boolean urgent,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String city
    ) {
        Page<CampaignSummaryResponse> page = campaignService.listPublic(pageable, ngoId, category, urgent, search, city);
        return ResponseEntity.ok(ApiResponse.success(page, "Campanhas publicas"));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ApiResponse<CampaignDetailResponse>> getPublic(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(campaignService.getPublic(id), "Campanha publica"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<Page<CampaignSummaryResponse>>> listForManagers(
        Pageable pageable,
        @RequestParam(required = false) Long ngoId,
        @RequestParam(required = false) CampaignStatus status,
        Principal principal
    ) {
        Page<CampaignSummaryResponse> page = campaignService.listForManagers(pageable, ngoId, status, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(page, "Campanhas"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<CampaignDetailResponse>> getForManager(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(campaignService.getForManager(id, principal.getName()), "Campanha"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<CampaignDetailResponse>> create(
        @Valid @RequestBody CampaignCreateRequest request,
        Principal principal
    ) {
        CampaignDetailResponse created = campaignService.create(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Campanha criada"));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<CampaignDetailResponse>> patch(
        @PathVariable Long id,
        @Valid @RequestBody CampaignPatchRequest request,
        Principal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(campaignService.patch(id, request, principal.getName()), "Campanha atualizada"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<CampaignDetailResponse>> cancel(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(campaignService.cancel(id, principal.getName()), "Campanha cancelada"));
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<CampaignDetailResponse>> addItem(
        @PathVariable Long id,
        @Valid @RequestBody CampaignItemRequest request,
        Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(campaignService.addItem(id, request, principal.getName()), "Item adicionado"));
    }

    @PatchMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<CampaignDetailResponse>> patchItem(
        @PathVariable Long id,
        @PathVariable Long itemId,
        @Valid @RequestBody CampaignItemPatchRequest request,
        Principal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(campaignService.patchItem(id, itemId, request, principal.getName()), "Item atualizado"));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<CampaignDetailResponse>> removeItem(
        @PathVariable Long id,
        @PathVariable Long itemId,
        Principal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(campaignService.removeItem(id, itemId, principal.getName()), "Item removido"));
    }

    @PostMapping("/{id}/updates")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<CampaignDetailResponse>> addNews(
        @PathVariable Long id,
        @Valid @RequestBody CampaignNewsRequest request,
        Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(campaignService.addNews(id, request, principal.getName()), "Atualizacao publicada"));
    }

    @DeleteMapping("/{id}/updates/{newsId}")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> removeNews(
        @PathVariable Long id,
        @PathVariable Long newsId,
        Principal principal
    ) {
        campaignService.removeNews(id, newsId, principal.getName());
        return ResponseEntity.ok(ApiResponse.successMessage("Atualizacao removida"));
    }
}
