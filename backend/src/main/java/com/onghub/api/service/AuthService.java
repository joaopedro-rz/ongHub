package com.onghub.api.service;

import com.onghub.api.dto.request.LoginRequest;
import com.onghub.api.dto.request.RegisterRequest;
import com.onghub.api.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(String refreshToken);
    void requestPasswordReset(String email);
    void resetPassword(String token, String password);
    void verifyEmail(String token);
}
