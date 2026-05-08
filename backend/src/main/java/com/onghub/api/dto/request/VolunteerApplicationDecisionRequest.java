package com.onghub.api.dto.request;

import com.onghub.api.entity.VolunteerApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record VolunteerApplicationDecisionRequest(
    @NotNull VolunteerApplicationStatus status
) {}
