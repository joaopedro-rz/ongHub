package com.onghub.api.repository;

import com.onghub.api.entity.VolunteerSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerScheduleRepository extends JpaRepository<VolunteerSchedule, Long> {
    List<VolunteerSchedule> findByOpportunity_IdOrderBySlotStartAsc(Long opportunityId);
}
