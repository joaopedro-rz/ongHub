package com.onghub.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onghub.api.dto.request.NgoRegisterRequest;
import com.onghub.api.dto.response.NgoResponse;
import com.onghub.api.entity.NgoStatus;
import com.onghub.api.exception.GlobalExceptionHandler;
import com.onghub.api.service.NgoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NgoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private NgoService ngoService;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        ngoService = mock(NgoService.class);
        NgoController controller = new NgoController(ngoService);
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void getPublicPermittedWithoutAuth() throws Exception {
        NgoResponse response = new NgoResponse(
            1L,
            "ONG",
            null,
            null,
            null,
            null,
            null,
            NgoStatus.ACTIVE,
            10L,
            null,
            null
        );
        when(ngoService.getPublicById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/ngos/1/public"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void registerReturnsCreated() throws Exception {
        NgoRegisterRequest request = new NgoRegisterRequest(
            "ONG",
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        NgoResponse response = new NgoResponse(
            1L,
            "ONG",
            null,
            null,
            null,
            null,
            null,
            NgoStatus.PENDING,
            10L,
            null,
            null
        );

        when(ngoService.registerNgo(anyString(), any(NgoRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/ngos")
                .principal(() -> "manager@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void updateStatusReturnsOk() throws Exception {
        NgoResponse response = new NgoResponse(
            1L,
            "ONG",
            null,
            null,
            null,
            null,
            null,
            NgoStatus.ACTIVE,
            10L,
            null,
            null
        );

        when(ngoService.updateStatus(eq(1L), eq(NgoStatus.ACTIVE))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/ngos/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ACTIVE\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }
}
