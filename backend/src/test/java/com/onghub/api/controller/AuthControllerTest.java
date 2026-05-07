package com.onghub.api.controller;

import com.onghub.api.dto.response.AuthResponse;
import com.onghub.api.dto.response.UserResponse;
import com.onghub.api.exception.*;
import com.onghub.api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;
    private AuthService authService;

    @BeforeEach
    void setup() {
        authService = mock(AuthService.class);
        AuthController controller = new AuthController(authService);
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        reset(authService);
    }

    @Test
    void registerReturnsApiResponse() throws Exception {
        when(authService.register(any())).thenReturn(buildAuthResponse());

        String payload = "{" +
            "\"name\":\"User\"," +
            "\"email\":\"user@example.com\"," +
            "\"password\":\"password\"," +
            "\"role\":\"DONOR\"" +
            "}";

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").value("access"));
    }

    @Test
    void loginReturnsApiResponse() throws Exception {
        when(authService.login(any())).thenReturn(buildAuthResponse());

        String payload = "{" +
            "\"email\":\"user@example.com\"," +
            "\"password\":\"password\"" +
            "}";

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.refreshToken").value("refresh"));
    }

    @Test
    void refreshReturnsApiResponse() throws Exception {
        when(authService.refresh(eq("refresh"))).thenReturn(buildAuthResponse());

        String payload = "{\"refreshToken\":\"refresh\"}";

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").value("access"));
    }

    @Test
    void invalidCredentialsReturnsApiError() throws Exception {
        when(authService.login(any())).thenThrow(new InvalidCredentialsException("Invalid credentials"));

        String payload = "{" +
            "\"email\":\"user@example.com\"," +
            "\"password\":\"wrong\"" +
            "}";

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void registerDuplicateEntryReturnsApiError() throws Exception {
        when(authService.register(any())).thenThrow(new DuplicateEntryException("Email exists"));

        String payload = "{" +
            "\"name\":\"User\"," +
            "\"email\":\"user@example.com\"," +
            "\"password\":\"password\"," +
            "\"role\":\"DONOR\"" +
            "}";

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_ENTRY"));
    }

    @Test
    void refreshTokenExpiredReturnsApiError() throws Exception {
        when(authService.refresh(eq("refresh"))).thenThrow(new TokenExpiredException("Expired"));

        String payload = "{\"refreshToken\":\"refresh\"}";

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("TOKEN_EXPIRED"));
    }

    private AuthResponse buildAuthResponse() {
        UserResponse userResponse = new UserResponse(
            1L,
            "User",
            "user@example.com",
            null,
            null,
            true,
            Set.of("DONOR")
        );
        return new AuthResponse("access", "refresh", userResponse);
    }
}
