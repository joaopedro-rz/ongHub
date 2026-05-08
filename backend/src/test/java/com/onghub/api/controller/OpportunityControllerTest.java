package com.onghub.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onghub.api.dto.request.VolunteerApplyRequest;
import com.onghub.api.dto.request.VolunteerApplicationDecisionRequest;
import com.onghub.api.dto.request.VolunteerOpportunityCreateRequest;
import com.onghub.api.dto.response.ApiResponse;
import com.onghub.api.dto.volunteer.VolunteerApiDtos;
import com.onghub.api.entity.*;
import com.onghub.api.service.VolunteerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OpportunityControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private VolunteerService volunteerService;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        volunteerService = Mockito.mock(VolunteerService.class);
        OpportunityController controller = new OpportunityController(volunteerService);

        SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver(sortResolver);

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setCustomArgumentResolvers(pageableResolver)
            .setControllerAdvice(new com.onghub.api.exception.GlobalExceptionHandler())
            .build();
    }

    @Test
    void listPublicReturns200() throws Exception {
        VolunteerApiDtos.OpportunitySummary summary = new VolunteerApiDtos.OpportunitySummary(
            1L,
            10L,
            "ONG",
            "Titulo",
            5,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1)
        );

        Page<VolunteerApiDtos.OpportunitySummary> page = new PageImpl<>(List.of(summary), PageRequest.of(0, 10), 1);
        when(volunteerService.listPublic(any(), eq(10L), eq(3L), eq("java"))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/opportunities")
                .param("ngoId", "10")
                .param("skillId", "3")
                .param("search", "java")
                .param("page", "0")
                .param("size", "10")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    void getPublicReturns200() throws Exception {
        VolunteerApiDtos.OpportunityDetailPublic detail = new VolunteerApiDtos.OpportunityDetailPublic(
            new VolunteerApiDtos.OpportunitySummary(
                1L,
                10L,
                "ONG",
                "Titulo",
                5,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1)
            ),
            "desc",
            "java,sql",
            5,
            List.of()
        );

        when(volunteerService.getPublic(1L)).thenReturn(detail);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/opportunities/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.summary.id").value(1));
    }

    @Test
    void applyReturns201() throws Exception {
        VolunteerApplyRequest req = new VolunteerApplyRequest("java");
        VolunteerApiDtos.ApplicationResponse resp = new VolunteerApiDtos.ApplicationResponse(
            2L,
            1L,
            3L,
            "vol@test.com",
            "PENDING",
            "java"
        );

        when(volunteerService.apply(eq(1L), any(VolunteerApplyRequest.class), eq("vol@test.com"))).thenReturn(resp);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/opportunities/1/apply")
                .principal(() -> "vol@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Inscricao enviada"));
    }

    @Test
    void decideReturns200() throws Exception {
        VolunteerApplicationDecisionRequest body = new VolunteerApplicationDecisionRequest(VolunteerApplicationStatus.APPROVED);

        VolunteerApiDtos.ApplicationResponse resp = new VolunteerApiDtos.ApplicationResponse(
            2L,
            1L,
            3L,
            "vol@test.com",
            "APPROVED",
            "java"
        );

        when(volunteerService.decide(eq(2L), any(VolunteerApplicationDecisionRequest.class), eq("manager@test.com"))).thenReturn(resp);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v1/opportunities/1/applications/2")
                .principal(() -> "manager@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Atualizado"));
    }

    @Test
    void mineReturns200() throws Exception {
        VolunteerApiDtos.ApplicationResponse resp = new VolunteerApiDtos.ApplicationResponse(
            2L,
            1L,
            3L,
            "vol@test.com",
            "PENDING",
            "java"
        );

        Page<VolunteerApiDtos.ApplicationResponse> page = new PageImpl<>(List.of(resp), PageRequest.of(0, 10), 1);
        when(volunteerService.listMine(any(), eq("vol@test.com"))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/volunteers/my-applications")
                .principal(() -> "vol@test.com")
                .param("page", "0")
                .param("size", "10")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content[0].id").value(2));
    }
}

