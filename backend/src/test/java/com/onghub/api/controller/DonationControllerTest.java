package com.onghub.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onghub.api.dto.request.FinancialDonationRequest;
import com.onghub.api.dto.request.MaterialDonationRequest;
import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.dto.response.DonationResponse;
import com.onghub.api.entity.*;
import com.onghub.api.service.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DonationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private DonationService donationService;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        donationService = Mockito.mock(DonationService.class);
        DonationController controller = new DonationController(donationService);

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new com.onghub.api.exception.GlobalExceptionHandler())
            .build();
    }

    @Test
    void createFinancialReturns201() throws Exception {
        FinancialDonationRequest request = new FinancialDonationRequest(
            10L,
            new BigDecimal("50.00"),
            "pix",
            null,
            null
        );

        DonationResponse response = new DonationResponse(
            1L,
            DonationType.FINANCIAL,
            DonationStatus.PENDING,
            10L,
            "Camp",
            new BigDecimal("50.00"),
            "pix",
            null,
            null,
            null,
            null,
            null,
            null,
            LocalDateTime.now(),
            null
        );

        when(donationService.createFinancial(any(FinancialDonationRequest.class), eq("donor@test.com"))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/donations/financial")
                .principal(() -> "donor@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Doacao registrada"))
            .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void confirmMaterialUpdatesAndReturns200() throws Exception {
        DonationResponse response = new DonationResponse(
            1L,
            DonationType.MATERIAL,
            DonationStatus.CONFIRMED,
            10L,
            "Camp",
            null,
            null,
            null,
            null,
            2,
            50L,
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "REC-XYZ"
        );

        when(donationService.confirm(eq(1L), eq("manager@test.com"))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v1/donations/1/confirm")
                .principal(() -> "manager@test.com")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Doacao confirmada"));
    }

    @Test
    void receiptEndpointReturnsPdf() throws Exception {
        when(donationService.downloadReceipt(1L, "donor@test.com")).thenReturn(new byte[] { 1, 2, 3 });

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/donations/1/receipt")
                .principal(() -> "donor@test.com")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_PDF))
            .andExpect(header().string("Content-Disposition", "attachment; filename=recibo-doacao.pdf"));
    }
}

