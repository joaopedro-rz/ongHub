package com.onghub.api.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record VolunteerOpportunityPatchRequest(
    @Size(max = 255) String title,
    String description,
    String skillsRequired,
    @Positive Integer slotsAvailable,
    Integer hoursPerWeek,
    LocalDate startDate,
    LocalDate endDate
) {}
