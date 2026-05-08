package com.onghub.api.mapper;

import com.onghub.api.dto.response.*;
import com.onghub.api.entity.Campaign;
import com.onghub.api.entity.CampaignItem;
import com.onghub.api.entity.CampaignUpdate;
import org.springframework.stereotype.Component;

@Component
public class CampaignDtoAssembler {

    public CampaignSummaryResponse toSummary(Campaign c) {
        return new CampaignSummaryResponse(
            c.getId(),
            c.getTitle(),
            c.getStatus(),
            c.getNgo().getId(),
            c.getNgo().getName(),
            c.getCategory(),
            c.isUrgent(),
            c.getCoverImageUrl(),
            c.getStartDate(),
            c.getEndDate(),
            c.getFinancialGoal()
        );
    }

    public CampaignDetailResponse toDetail(Campaign c) {
        return new CampaignDetailResponse(
            c.getId(),
            c.getTitle(),
            c.getDescription(),
            c.getFinancialGoal(),
            c.getStartDate(),
            c.getEndDate(),
            c.getCoverImageUrl(),
            c.getStatus(),
            c.isUrgent(),
            c.getCategory(),
            c.getNgo().getId(),
            c.getNgo().getName(),
            c.getItems().stream().map(this::toItem).toList(),
            c.getUpdates().stream().map(this::toUpdate).toList()
        );
    }

    public CampaignItemResponse toItem(CampaignItem i) {
        return new CampaignItemResponse(
            i.getId(),
            i.getItemName(),
            i.getCategory(),
            i.getQuantityNeeded(),
            i.getQuantityReceived(),
            i.getUnit()
        );
    }

    public CampaignUpdateResponse toUpdate(CampaignUpdate u) {
        return new CampaignUpdateResponse(u.getId(), u.getTitle(), u.getBody(), u.getCreatedAt());
    }
}
