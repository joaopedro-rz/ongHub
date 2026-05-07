package com.onghub.api.controller;

import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.dto.response.UserResponse;
import com.onghub.api.service.UserService;
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
}
