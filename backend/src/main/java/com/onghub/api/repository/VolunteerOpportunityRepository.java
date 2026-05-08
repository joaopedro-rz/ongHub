package com.onghub.api.repository;

import com.onghub.api.entity.NgoStatus;
import com.onghub.api.entity.VolunteerOpportunity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VolunteerOpportunityRepository extends JpaRepository<VolunteerOpportunity, Long> {

    @EntityGraph(attributePaths = { "ngo", "ngo.manager" })
    @Override
    Optional<VolunteerOpportunity> findById(Long id);

    Page<VolunteerOpportunity> findByNgo_Id(Long ngoId, Pageable pageable);

    @EntityGraph(attributePaths = { "ngo" })
    @Query("""
        SELECT o FROM VolunteerOpportunity o
        JOIN o.ngo n
        WHERE n.status = :st
          AND (:ngoId IS NULL OR n.id = :ngoId)
          AND (:skillName IS NULL OR LOWER(COALESCE(o.skillsRequired,'')) LIKE LOWER(CONCAT('%', :skillName, '%')))
          AND (
                :search IS NULL OR
                LOWER(o.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(COALESCE(o.description,'')) LIKE LOWER(CONCAT('%', :search, '%'))
          )
    """)
    Page<VolunteerOpportunity> findPublicListing(
        @Param("st") NgoStatus status,
        @Param("ngoId") Long ngoId,
        @Param("skillName") String skillName,
        @Param("search") String search,
        Pageable pageable
    );
}
