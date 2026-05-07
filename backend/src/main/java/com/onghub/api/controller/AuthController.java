package com.onghub.api.controller;

import com.onghub.api.dto.request.LoginRequest;
import com.onghub.api.dto.request.RegisterRequest;
import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.dto.response.AuthResponse;
import com.onghub.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(authService.register(request), "Registro realizado com sucesso"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login realizado com sucesso"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(request.refreshToken()), "Token atualizado"));
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        authService.requestPasswordReset(request.email());
        return ResponseEntity.accepted().body(ApiResponse.successMessage("Email de recuperacao enviado"));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody PasswordUpdateRequest request) {
        authService.resetPassword(request.token(), request.password());
        return ResponseEntity.ok(ApiResponse.successMessage("Senha atualizada"));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request.token());
        return ResponseEntity.ok(ApiResponse.successMessage("Email confirmado"));
    }

    public record RefreshTokenRequest(@jakarta.validation.constraints.NotBlank String refreshToken) {}

    public record PasswordResetRequest(@jakarta.validation.constraints.Email @jakarta.validation.constraints.NotBlank String email) {}

    public record PasswordUpdateRequest(
        @jakarta.validation.constraints.NotBlank String token,
        @jakarta.validation.constraints.NotBlank String password
    ) {}

    public record VerifyEmailRequest(@jakarta.validation.constraints.NotBlank String token) {}
}
