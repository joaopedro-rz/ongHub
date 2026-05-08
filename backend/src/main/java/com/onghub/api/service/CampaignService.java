package com.onghub.api.service;

import com.onghub.api.dto.request.*;
import com.onghub.api.dto.response.CampaignDetailResponse;
import com.onghub.api.dto.response.CampaignSummaryResponse;
import com.onghub.api.entity.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CampaignService {

    Page<CampaignSummaryResponse> listPublic(
        Pageable pageable,
        Long ngoId,
        String category,
        Boolean urgent,
        String search,
        String city
    );

    CampaignDetailResponse getPublic(Long id);

    Page<CampaignSummaryResponse> listForManagers(Pageable pageable, Long ngoId, CampaignStatus status, String principalEmail);

    CampaignDetailResponse getForManager(Long id, String principalEmail);

    CampaignDetailResponse create(CampaignCreateRequest request, String principalEmail);

    CampaignDetailResponse patch(Long id, CampaignPatchRequest request, String principalEmail);

    CampaignDetailResponse cancel(Long id, String principalEmail);

    CampaignDetailResponse addItem(Long campaignId, CampaignItemRequest request, String principalEmail);

    CampaignDetailResponse patchItem(Long campaignId, Long itemId, CampaignItemPatchRequest request, String principalEmail);

    CampaignDetailResponse removeItem(Long campaignId, Long itemId, String principalEmail);

    CampaignDetailResponse addNews(Long campaignId, CampaignNewsRequest request, String principalEmail);

    void removeNews(Long campaignId, Long newsId, String principalEmail);
}
