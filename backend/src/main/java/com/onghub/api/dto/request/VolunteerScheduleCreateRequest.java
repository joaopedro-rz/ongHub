package com.onghub.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record VolunteerScheduleCreateRequest(
    @NotNull LocalDateTime slotStart,
    @NotNull LocalDateTime slotEnd,
    @Size(max = 255) String title,
    Long volunteerUserId
) {}
