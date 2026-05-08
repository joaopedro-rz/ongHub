package com.onghub.api.repository;

import com.onghub.api.entity.CampaignItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignItemRepository extends JpaRepository<CampaignItem, Long> {
    List<CampaignItem> findByCampaign_Id(Long campaignId);
}
