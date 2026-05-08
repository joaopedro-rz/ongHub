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
@RequestMapping("/api/v1/volunteer")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping("/opportunities/public")
    public ResponseEntity<ApiResponse<Page<VolunteerApiDtos.OpportunitySummary>>> listPublic(Pageable pageable, @RequestParam(required = false) Long ngoId) {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.listPublic(pageable, ngoId), "Vagas publicas"));
    }

    @GetMapping("/opportunities/public/{id}")
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

    @GetMapping("/opportunities")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<Page<VolunteerApiDtos.OpportunityManagement>>> listManaged(
        Pageable pageable,
        @RequestParam(required = false) Long ngoId,
        Principal principal
    ) {
        Page<VolunteerApiDtos.OpportunityManagement> page = volunteerService.listManaged(pageable, ngoId, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(page, "Vagas"));
    }

    @PatchMapping("/opportunities/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<VolunteerApiDtos.OpportunityManagement>> patch(
        @PathVariable Long id,
        @Valid @RequestBody VolunteerOpportunityPatchRequest request,
        Principal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.patch(id, request, principal.getName()), "Vaga atualizada"));
    }

    @DeleteMapping("/opportunities/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Principal principal) {
        volunteerService.delete(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.successMessage("Vaga removida"));
    }

    @PostMapping("/opportunities/{id}/applications")
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

    @GetMapping("/opportunities/{id}/applications")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<Page<VolunteerApiDtos.ApplicationResponse>>> listApplications(
        @PathVariable Long id,
        Pageable pageable,
        Principal principal
    ) {
        Page<VolunteerApiDtos.ApplicationResponse> page = volunteerService.listApplications(pageable, id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(page, "Inscricoes"));
    }

    @PatchMapping("/applications/{applicationId}/decision")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<VolunteerApiDtos.ApplicationResponse>> decide(
        @PathVariable Long applicationId,
        @Valid @RequestBody VolunteerApplicationDecisionRequest request,
        Principal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(volunteerService.decide(applicationId, request, principal.getName()), "Atualizado"));
    }

    @GetMapping("/applications/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<VolunteerApiDtos.ApplicationResponse>>> mine(Pageable pageable, Principal principal) {
        Page<VolunteerApiDtos.ApplicationResponse> page = volunteerService.listMine(pageable, principal.getName());
        return ResponseEntity.ok(ApiResponse.success(page, "Minhas inscricoes"));
    }

    @PostMapping("/opportunities/{id}/schedules")
    @PreAuthorize("hasAnyRole('ADMIN','ONG_MANAGER')")
    public ResponseEntity<ApiResponse<VolunteerApiDtos.ScheduleResponse>> addSchedule(
        @PathVariable Long id,
        @Valid @RequestBody VolunteerScheduleCreateRequest request,
        Principal principal
    ) {
        VolunteerApiDtos.ScheduleResponse created = volunteerService.addSchedule(id, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Escala criada"));
    }
}
