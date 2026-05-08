package com.onghub.api.service.impl;

import com.onghub.api.dto.request.FinancialDonationRequest;
import com.onghub.api.dto.request.MaterialDonationRequest;
import com.onghub.api.dto.response.DonationResponse;
import com.onghub.api.entity.*;
import com.onghub.api.exception.BadRequestException;
import com.onghub.api.exception.ResourceNotFoundException;
import com.onghub.api.mapper.DonationMapper;
import com.onghub.api.repository.CampaignItemRepository;
import com.onghub.api.repository.CampaignRepository;
import com.onghub.api.repository.FinancialDonationRepository;
import com.onghub.api.repository.MaterialDonationRepository;
import com.onghub.api.repository.DonationReceiptRepository;
import com.onghub.api.repository.DonationRepository;
import com.onghub.api.repository.UserRepository;
import com.onghub.api.security.SecurityUtils;
import com.onghub.api.service.DonationService;
import com.onghub.api.util.DonationPdfWriter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final DonationReceiptRepository donationReceiptRepository;
    private final FinancialDonationRepository financialDonationRepository;
    private final MaterialDonationRepository materialDonationRepository;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignItemRepository campaignItemRepository;
    private final DonationMapper donationMapper;

    public DonationServiceImpl(
        DonationRepository donationRepository,
        DonationReceiptRepository donationReceiptRepository,
        FinancialDonationRepository financialDonationRepository,
        MaterialDonationRepository materialDonationRepository,
        UserRepository userRepository,
        CampaignRepository campaignRepository,
        CampaignItemRepository campaignItemRepository,
        DonationMapper donationMapper
    ) {
        this.donationRepository = donationRepository;
        this.donationReceiptRepository = donationReceiptRepository;
        this.financialDonationRepository = financialDonationRepository;
        this.materialDonationRepository = materialDonationRepository;
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
        this.campaignItemRepository = campaignItemRepository;
        this.donationMapper = donationMapper;
    }

    @Override
    public DonationResponse createFinancial(FinancialDonationRequest request, String donorEmail) {
        User donor = loadUser(donorEmail);
        Campaign campaign = campaignRepository.findById(request.campaignId())
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new BadRequestException("Campaign not accepting donations");
        }

        Donation d = new Donation();
        d.setDonationType(DonationType.FINANCIAL);
        d.setDonor(donor);
        d.setCampaign(campaign);
        d.setAmount(request.amount());
        d.setPaymentMethod(request.paymentMethod());
        d.setProofUrl(request.proofUrl());
        d.setNotes(request.notes());
        d.setStatus(DonationStatus.PENDING);
        Donation saved = donationRepository.save(d);

        FinancialDonation fd = new FinancialDonation();
        fd.setDonation(saved);
        fd.setAmount(saved.getAmount());
        fd.setPaymentMethod(saved.getPaymentMethod());
        fd.setProofUrl(saved.getProofUrl());
        fd.setNotes(saved.getNotes());
        financialDonationRepository.save(fd);

        return toDto(saved, false);
    }

    @Override
    public DonationResponse createMaterial(MaterialDonationRequest request, String donorEmail) {
        User donor = loadUser(donorEmail);
        Campaign campaign = campaignRepository.findById(request.campaignId())
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new BadRequestException("Campaign not accepting donations");
        }

        CampaignItem linkedItem = null;
        if (request.campaignItemId() != null) {
            linkedItem = campaignItemRepository.findById(request.campaignItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign item not found"));
            if (!linkedItem.getCampaign().getId().equals(campaign.getId())) {
                throw new BadRequestException("Item does not belong to campaign");
            }
        }

        Donation d = new Donation();
        d.setDonationType(DonationType.MATERIAL);
        d.setDonor(donor);
        d.setCampaign(campaign);
        d.setMaterialDescription(request.materialDescription().trim());
        d.setQuantity(request.quantity());
        d.setCampaignItem(linkedItem);
        d.setProofUrl(request.proofUrl());
        d.setNotes(request.notes());
        d.setStatus(DonationStatus.PENDING);
        Donation saved = donationRepository.save(d);

        MaterialDonation md = new MaterialDonation();
        md.setDonation(saved);
        md.setMaterialDescription(saved.getMaterialDescription());
        md.setQuantity(saved.getQuantity());
        md.setCampaignItem(saved.getCampaignItem());
        md.setProofUrl(saved.getProofUrl());
        md.setNotes(saved.getNotes());
        materialDonationRepository.save(md);

        return toDto(saved, false);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DonationResponse> listMine(Pageable pageable, String donorEmail) {
        User donor = loadUser(donorEmail);
        return donationRepository.findByDonor_Id(donor.getId(), pageable).map(d -> toDto(d, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DonationResponse> listForCampaign(Pageable pageable, Long campaignId, String principalEmail) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        assertManageCampaign(campaign, principalEmail);
        return donationRepository.findByCampaign_Id(campaignId, pageable).map(d -> toDto(d, false));
    }

    @Override
    public DonationResponse confirm(Long donationId, String principalEmail) {
        Donation d = donationRepository.findById(donationId)
            .orElseThrow(() -> new ResourceNotFoundException("Donation not found"));
        if (d.getStatus() != DonationStatus.PENDING) {
            throw new BadRequestException("Donation cannot be confirmed");
        }
        assertManageCampaign(d.getCampaign(), principalEmail);

        if (d.getDonationType() == DonationType.MATERIAL && d.getCampaignItem() != null && d.getQuantity() != null) {
            CampaignItem item = d.getCampaignItem();
            int next = item.getQuantityReceived() + d.getQuantity();
            if (next > item.getQuantityNeeded()) {
                next = item.getQuantityNeeded();
            }
            item.setQuantityReceived(next);
            campaignItemRepository.save(item);
        }

        d.setStatus(DonationStatus.CONFIRMED);
        d.setConfirmedAt(LocalDateTime.now());
        donationRepository.save(d);

        DonationReceipt receipt = new DonationReceipt();
        receipt.setDonation(d);
        receipt.setReceiptNumber("REC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        donationReceiptRepository.save(receipt);

        return toDto(d, true);
    }

    @Override
    public DonationResponse reject(Long donationId, String principalEmail) {
        Donation d = donationRepository.findById(donationId)
            .orElseThrow(() -> new ResourceNotFoundException("Donation not found"));
        if (d.getStatus() != DonationStatus.PENDING) {
            throw new BadRequestException("Donation cannot be rejected");
        }
        assertManageCampaign(d.getCampaign(), principalEmail);
        d.setStatus(DonationStatus.REJECTED);
        return toDto(donationRepository.save(d), false);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadReceipt(Long donationId, String principalEmail) {
        Donation d = donationRepository.findById(donationId)
            .orElseThrow(() -> new ResourceNotFoundException("Donation not found"));
        assertReceiptAccess(d, principalEmail);
        if (d.getStatus() != DonationStatus.CONFIRMED) {
            throw new BadRequestException("Receipt not available");
        }
        DonationReceipt receipt = donationReceiptRepository.findByDonation_Id(d.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));
        return DonationPdfWriter.build(d, receipt);
    }

    private User loadUser(String email) {
        return userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void assertManageCampaign(Campaign campaign, String principalEmail) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        Ngo ngo = campaign.getNgo();
        if (ngo.getManager() == null || !ngo.getManager().getEmail().equalsIgnoreCase(principalEmail)) {
            throw new ResourceNotFoundException("Campaign not found");
        }
    }

    private void assertReceiptAccess(Donation d, String principalEmail) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        if (d.getDonor().getEmail().equalsIgnoreCase(principalEmail)) {
            return;
        }
        try {
            assertManageCampaign(d.getCampaign(), principalEmail);
        } catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException("Donation not found");
        }
    }

    private DonationResponse toDto(Donation d, boolean loadReceiptNumber) {
        String receiptNumber = null;
        if (loadReceiptNumber && d.getStatus() == DonationStatus.CONFIRMED) {
            receiptNumber = donationReceiptRepository.findByDonation_Id(d.getId())
                .map(DonationReceipt::getReceiptNumber)
                .orElse(null);
        }
        return donationMapper.toDonationResponse(d, receiptNumber);
    }
}
