package com.onghub.api.controller;

import com.onghub.api.dto.request.NgoRegisterRequest;
import com.onghub.api.dto.request.NgoUpdateRequest;
import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.dto.response.NgoResponse;
import com.onghub.api.dto.response.NgoSummaryResponse;
import com.onghub.api.entity.NgoStatus;
import com.onghub.api.service.NgoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/ngos")
public class NgoController {

    private final NgoService ngoService;

    public NgoController(NgoService ngoService) {
        this.ngoService = ngoService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NgoResponse>> register(@Valid @RequestBody NgoRegisterRequest request, Principal principal) {
        NgoResponse created = ngoService.registerNgo(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(created, "ONG cadastrada"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NgoSummaryResponse>>> list(
        Pageable pageable,
        @RequestParam(required = false) String managerEmail,
        @RequestParam(required = false) NgoStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.success(ngoService.list(pageable, managerEmail, status), "Lista de ONGs"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NgoResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ngoService.getById(id), "ONG carregada"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NgoResponse>> update(
        @PathVariable Long id,
        @Valid @RequestBody NgoUpdateRequest request,
        Principal principal
    ) {
        return ResponseEntity.ok(ApiResponse.success(ngoService.update(id, principal.getName(), request), "ONG atualizada"));
    }

    @GetMapping("/{id}/public")
    public ResponseEntity<ApiResponse<NgoResponse>> getPublic(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ngoService.getPublicById(id), "ONG publica"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NgoResponse>> updateStatus(
        @PathVariable Long id,
        @RequestBody @NotNull StatusUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(ngoService.updateStatus(id, request.status()), "Status atualizado"));
    }

    public record StatusUpdateRequest(@NotNull NgoStatus status) {}
}
