package com.onghub.api.repository;

import com.onghub.api.entity.VolunteerApplication;
import com.onghub.api.entity.VolunteerApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VolunteerApplicationRepository extends JpaRepository<VolunteerApplication, Long> {

    @EntityGraph(attributePaths = { "opportunity", "opportunity.ngo", "opportunity.ngo.manager", "volunteer" })
    @Override
    Optional<VolunteerApplication> findById(Long id);

    Optional<VolunteerApplication> findByOpportunity_IdAndVolunteer_Id(Long opportunityId, Long volunteerId);

    Page<VolunteerApplication> findByOpportunity_Id(Long opportunityId, Pageable pageable);

    Page<VolunteerApplication> findByVolunteer_Id(Long volunteerId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM VolunteerApplication a JOIN a.opportunity o WHERE o.ngo.id = :ngoId AND a.status = :st")
    long countByNgoAndStatus(@Param("ngoId") Long ngoId, @Param("st") VolunteerApplicationStatus st);
}
