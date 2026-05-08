package com.onghub.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record VolunteerOpportunityCreateRequest(
    @NotNull Long ngoId,
    @NotBlank @Size(max = 255) String title,
    String description,
    String skillsRequired,
    @Positive int slotsAvailable,
    Integer hoursPerWeek,
    LocalDate startDate,
    LocalDate endDate
) {}
