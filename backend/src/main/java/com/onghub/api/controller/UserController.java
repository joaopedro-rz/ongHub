package com.onghub.api.controller;

import com.onghub.api.dto.request.UserProfileUpdateRequest;
import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.dto.response.UserResponse;
import com.onghub.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(
            ApiResponse.success(userService.getCurrentUser(principal.getName()), "Perfil carregado")
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
        Principal principal,
        @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(userService.updateProfile(principal.getName(), request), "Perfil atualizado")
        );
    }

    @PostMapping("/me/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivate(Principal principal) {
        userService.deactivateAccount(principal.getName());
        return ResponseEntity.ok(ApiResponse.successMessage("Conta desativada"));
    }
}
