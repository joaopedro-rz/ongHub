package com.onghub.api.controller;

import com.onghub.api.dto.request.*;
import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.dto.volunteer.VolunteerApiDtos;
import com.onghub.api.service.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
public class OpportunityController {

    private final VolunteerService volunteerService;

    public OpportunityController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping("/opportunities")
    public ResponseEntity<ApiResponse<Page<VolunteerApiDtos.OpportunitySummary>>> listPublic(
        Pageable pageable,
        @RequestParam(required = false) Long ngoId,
        @RequestParam(required = false) Long skillId,
        @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.listPublic(pageable, ngoId, skillId, search), "Vagas publicas"));
    }

    @GetMapping("/opportunities/{id}")
    public ResponseEntity<ApiResponse<VolunteerApiDtos.OpportunityDetailPublic>> getPublic(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.getPublic(id), "Vaga"));
    }

    @PostMapping("/opportunities")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<VolunteerApiDtos.OpportunityManagement>> create(
        @Valid @RequestBody VolunteerOpportunityCreateRequest request,
        Principal principal
    ) {
        VolunteerApiDtos.OpportunityManagement created = volunteerService.create(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Vaga criada"));
    }

    @PostMapping("/opportunities/{id}/apply")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<VolunteerApiDtos.ApplicationResponse>> apply(
        @PathVariable Long id,
        @RequestBody(required = false) VolunteerApplyRequest request,
        Principal principal
    ) {
        VolunteerApplyRequest body = request != null ? request : new VolunteerApplyRequest(null);
        VolunteerApiDtos.ApplicationResponse created = volunteerService.apply(id, body, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Inscricao enviada"));
    }

    @PatchMapping("/opportunities/{id}/applications/{applicationId}")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<VolunteerApiDtos.ApplicationResponse>> decide(
        @PathVariable Long id,
        @PathVariable Long applicationId,
        @Valid @RequestBody VolunteerApplicationDecisionRequest request,
        Principal principal
    ) {
        // `id` is kept for route compliance; service validates the opportunity via the application itself.
        VolunteerApiDtos.ApplicationResponse updated = volunteerService.decide(applicationId, request, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(updated, "Atualizado"));
    }

    @GetMapping("/volunteers/my-applications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<VolunteerApiDtos.ApplicationResponse>>> mine(
        Pageable pageable,
        Principal principal
    ) {
        Page<VolunteerApiDtos.ApplicationResponse> page = volunteerService.listMine(pageable, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(page, "Minhas inscricoes"));
    }
}

