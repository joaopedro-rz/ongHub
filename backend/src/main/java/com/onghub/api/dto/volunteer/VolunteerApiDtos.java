package com.onghub.api.dto.volunteer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class VolunteerApiDtos {

    private VolunteerApiDtos() {}

    public record OpportunitySummary(
        Long id,
        Long ngoId,
        String ngoName,
        String title,
        int slotsAvailable,
        LocalDate startDate,
        LocalDate endDate
    ) {}

    public record OpportunityDetailPublic(
        OpportunitySummary summary,
        String description,
        String skillsRequired,
        Integer hoursPerWeek,
        List<ScheduleResponse> schedules
    ) {}

    public record OpportunityManagement(
        Long id,
        Long ngoId,
        String title,
        String description,
        String skillsRequired,
        int slotsAvailable,
        Integer hoursPerWeek,
        LocalDate startDate,
        LocalDate endDate
    ) {}

    public record ScheduleResponse(
        Long id,
        LocalDateTime slotStart,
        LocalDateTime slotEnd,
        String title,
        Long volunteerUserId
    ) {}

    public record ApplicationResponse(
        Long id,
        Long opportunityId,
        Long volunteerUserId,
        String volunteerEmail,
        String status,
        String skillsNote
    ) {}
}
