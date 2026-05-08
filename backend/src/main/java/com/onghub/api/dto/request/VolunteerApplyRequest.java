package com.onghub.api.dto.request;

import jakarta.validation.constraints.Size;

public record VolunteerApplyRequest(
    @Size(max = 2000) String skillsNote
) {}
