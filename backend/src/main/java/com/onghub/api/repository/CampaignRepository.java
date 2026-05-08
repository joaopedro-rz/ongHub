package com.onghub.api.repository;

import com.onghub.api.entity.Campaign;
import com.onghub.api.entity.CampaignStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {

    long countByNgo_Id(Long ngoId);

    long countByNgo_IdAndStatus(Long ngoId, CampaignStatus status);

    @EntityGraph(attributePaths = { "ngo", "ngo.manager", "ngo.address" })
    @Override
    Optional<Campaign> findById(Long id);
}
