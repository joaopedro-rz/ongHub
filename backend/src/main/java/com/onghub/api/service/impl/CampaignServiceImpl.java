package com.onghub.api.service.impl;

import com.onghub.api.dto.request.*;
import com.onghub.api.dto.response.CampaignDetailResponse;
import com.onghub.api.dto.response.CampaignSummaryResponse;
import com.onghub.api.entity.*;
import com.onghub.api.exception.ResourceNotFoundException;
import com.onghub.api.mapper.CampaignDtoAssembler;
import com.onghub.api.repository.CampaignRepository;
import com.onghub.api.repository.NgoRepository;
import com.onghub.api.repository.spec.CampaignSpecifications;
import com.onghub.api.security.SecurityUtils;
import com.onghub.api.service.CampaignService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final NgoRepository ngoRepository;
    private final CampaignDtoAssembler assembler;

    public CampaignServiceImpl(CampaignRepository campaignRepository, NgoRepository ngoRepository, CampaignDtoAssembler assembler) {
        this.campaignRepository = campaignRepository;
        this.ngoRepository = ngoRepository;
        this.assembler = assembler;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CampaignSummaryResponse> listPublic(
        Pageable pageable,
        Long ngoId,
        String category,
        Boolean urgent,
        String search,
        String city
    ) {
        Specification<Campaign> spec = Specification.where(CampaignSpecifications.publiclyListed())
            .and(CampaignSpecifications.hasNgoId(ngoId))
            .and(CampaignSpecifications.categoryContains(category))
            .and(CampaignSpecifications.isUrgent(urgent))
            .and(CampaignSpecifications.searchText(search))
            .and(CampaignSpecifications.localityCity(city));
        return campaignRepository.findAll(spec, pageable).map(assembler::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignDetailResponse getPublic(Long id) {
        Campaign c = campaignRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        if (c.getStatus() != CampaignStatus.ACTIVE || c.getNgo().getStatus() != NgoStatus.ACTIVE) {
            throw new ResourceNotFoundException("Campaign not found");
        }
        return toDetail(c);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CampaignSummaryResponse> listForManagers(Pageable pageable, Long ngoId, CampaignStatus status, String principalEmail) {
        Specification<Campaign> spec = Specification.where(CampaignSpecifications.hasNgoId(ngoId))
            .and(CampaignSpecifications.hasCampaignStatus(status));
        if (!SecurityUtils.isAdmin()) {
            spec = spec.and(CampaignSpecifications.managedByEmail(principalEmail));
        }
        return campaignRepository.findAll(spec, pageable).map(assembler::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignDetailResponse getForManager(Long id, String principalEmail) {
        Campaign c = campaignRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        assertManageNgo(c.getNgo(), principalEmail);
        return toDetail(c);
    }

    @Override
    public CampaignDetailResponse create(CampaignCreateRequest request, String principalEmail) {
        Ngo ngo = ngoRepository.findById(request.ngoId())
            .orElseThrow(() -> new ResourceNotFoundException("Ngo not found"));
        assertManageNgo(ngo, principalEmail);

        Campaign c = new Campaign();
        c.setNgo(ngo);
        c.setTitle(request.title().trim());
        c.setDescription(request.description());
        c.setFinancialGoal(request.financialGoal());
        c.setStartDate(request.startDate());
        c.setEndDate(request.endDate());
        c.setCoverImageUrl(request.coverImageUrl());
        c.setStatus(request.status() != null ? request.status() : CampaignStatus.DRAFT);
        c.setUrgent(request.urgent());
        c.setCategory(request.category());
        return toDetail(campaignRepository.save(c));
    }

    @Override
    public CampaignDetailResponse patch(Long id, CampaignPatchRequest request, String principalEmail) {
        Campaign c = loadManaged(id, principalEmail);
        if (request.title() != null) {
            c.setTitle(request.title().trim());
        }
        if (request.description() != null) {
            c.setDescription(request.description());
        }
        if (request.financialGoal() != null) {
            c.setFinancialGoal(request.financialGoal());
        }
        if (request.startDate() != null) {
            c.setStartDate(request.startDate());
        }
        if (request.endDate() != null) {
            c.setEndDate(request.endDate());
        }
        if (request.coverImageUrl() != null) {
            c.setCoverImageUrl(request.coverImageUrl());
        }
        if (request.status() != null) {
            c.setStatus(request.status());
        }
        if (request.urgent() != null) {
            c.setUrgent(request.urgent());
        }
        if (request.category() != null) {
            c.setCategory(request.category());
        }
        return toDetail(campaignRepository.save(c));
    }

    @Override
    public CampaignDetailResponse cancel(Long id, String principalEmail) {
        Campaign c = loadManaged(id, principalEmail);
        c.setStatus(CampaignStatus.CANCELLED);
        return toDetail(campaignRepository.save(c));
    }

    @Override
    public CampaignDetailResponse addItem(Long campaignId, CampaignItemRequest request, String principalEmail) {
        Campaign c = loadManaged(campaignId, principalEmail);
        CampaignItem item = new CampaignItem();
        item.setCampaign(c);
        item.setItemName(request.itemName().trim());
        item.setCategory(request.category());
        item.setQuantityNeeded(request.quantityNeeded());
        item.setQuantityReceived(0);
        item.setUnit(request.unit());
        c.getItems().add(item);
        return toDetail(campaignRepository.save(c));
    }

    @Override
    public CampaignDetailResponse patchItem(Long campaignId, Long itemId, CampaignItemPatchRequest request, String principalEmail) {
        Campaign c = loadManaged(campaignId, principalEmail);
        CampaignItem item = findItem(c, itemId);
        if (request.itemName() != null) {
            item.setItemName(request.itemName().trim());
        }
        if (request.category() != null) {
            item.setCategory(request.category());
        }
        if (request.quantityNeeded() != null) {
            item.setQuantityNeeded(request.quantityNeeded());
        }
        if (request.quantityReceived() != null) {
            item.setQuantityReceived(request.quantityReceived());
        }
        if (request.unit() != null) {
            item.setUnit(request.unit());
        }
        return toDetail(campaignRepository.save(c));
    }

    @Override
    public CampaignDetailResponse removeItem(Long campaignId, Long itemId, String principalEmail) {
        Campaign c = loadManaged(campaignId, principalEmail);
        CampaignItem item = findItem(c, itemId);
        c.getItems().remove(item);
        return toDetail(campaignRepository.save(c));
    }

    @Override
    public CampaignDetailResponse addNews(Long campaignId, CampaignNewsRequest request, String principalEmail) {
        Campaign c = loadManaged(campaignId, principalEmail);
        CampaignUpdate update = new CampaignUpdate();
        update.setCampaign(c);
        update.setTitle(request.title());
        update.setBody(request.body());
        c.getUpdates().add(update);
        return toDetail(campaignRepository.save(c));
    }

    @Override
    public void removeNews(Long campaignId, Long newsId, String principalEmail) {
        Campaign c = loadManaged(campaignId, principalEmail);
        CampaignUpdate news = c.getUpdates().stream()
            .filter(u -> u.getId() != null && u.getId().equals(newsId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Update not found"));
        c.getUpdates().remove(news);
        campaignRepository.save(c);
    }

    private Campaign loadManaged(Long id, String principalEmail) {
        Campaign c = campaignRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        assertManageNgo(c.getNgo(), principalEmail);
        return c;
    }

    private static CampaignItem findItem(Campaign c, Long itemId) {
        return c.getItems().stream()
            .filter(i -> i.getId() != null && i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }

    private void assertManageNgo(Ngo ngo, String principalEmail) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        if (ngo.getManager() == null || !ngo.getManager().getEmail().equalsIgnoreCase(principalEmail)) {
            throw new ResourceNotFoundException("Ngo not found");
        }
    }

    private CampaignDetailResponse toDetail(Campaign c) {
        c.getItems().size();
        c.getUpdates().size();
        return assembler.toDetail(c);
    }
}
