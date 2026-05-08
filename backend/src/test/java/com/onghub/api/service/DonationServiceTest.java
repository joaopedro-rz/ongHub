package com.onghub.api.service;

import com.onghub.api.dto.request.FinancialDonationRequest;
import com.onghub.api.dto.request.MaterialDonationRequest;
import com.onghub.api.dto.response.DonationResponse;
import com.onghub.api.entity.*;
import com.onghub.api.exception.BadRequestException;
import com.onghub.api.mapper.DonationMapper;
import com.onghub.api.repository.*;
import com.onghub.api.service.impl.DonationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private DonationReceiptRepository donationReceiptRepository;

    @Mock
    private FinancialDonationRepository financialDonationRepository;

    @Mock
    private MaterialDonationRepository materialDonationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private CampaignItemRepository campaignItemRepository;

    @Mock
    private DonationMapper donationMapper;

    private DonationService donationService;

    @BeforeEach
    void setup() {
        donationService = new DonationServiceImpl(
            donationRepository,
            donationReceiptRepository,
            financialDonationRepository,
            materialDonationRepository,
            userRepository,
            campaignRepository,
            campaignItemRepository,
            donationMapper
        );
    }

    @Test
    void createFinancialPersistsFinancialDonation() {
        User donor = new User();
        donor.setId(5L);
        donor.setEmail("donor@test.com");

        when(userRepository.findByEmail("donor@test.com")).thenReturn(Optional.of(donor));

        Campaign campaign = new Campaign();
        campaign.setId(10L);
        campaign.setStatus(CampaignStatus.ACTIVE);
        when(campaignRepository.findById(10L)).thenReturn(Optional.of(campaign));

        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> {
            Donation d = inv.getArgument(0);
            d.setId(100L);
            return d;
        });

        when(financialDonationRepository.save(any(FinancialDonation.class))).thenAnswer(inv -> inv.getArgument(0));

        DonationResponse stubResponse = new DonationResponse(
            100L,
            DonationType.FINANCIAL,
            DonationStatus.PENDING,
            10L,
            "Camp",
            new BigDecimal("50.00"),
            "pix",
            null, // proofUrl
            null, // materialDescription
            null, // quantity
            null, // campaignItemId
            null, // notes
            null, // confirmedAt
            LocalDateTime.now(), // createdAt
            null // receiptNumber
        );
        when(donationMapper.toDonationResponse(any(Donation.class), isNull())).thenReturn(stubResponse);

        FinancialDonationRequest request = new FinancialDonationRequest(
            10L,
            new BigDecimal("50.00"),
            "pix",
            null,
            null
        );

        DonationResponse response = donationService.createFinancial(request, "donor@test.com");

        assertEquals(DonationType.FINANCIAL, response.donationType());
        verify(donationRepository).save(any(Donation.class));
        verify(financialDonationRepository).save(any(FinancialDonation.class));
    }

    @Test
    void confirmMaterialUpdatesQuantityAndCreatesReceipt() {
        User manager = new User();
        manager.setId(20L);
        manager.setEmail("manager@test.com");

        Ngo ngo = new Ngo();
        ngo.setId(30L);
        ngo.setManager(manager);

        Campaign campaign = new Campaign();
        campaign.setId(40L);
        campaign.setNgo(ngo);
        campaign.setStatus(CampaignStatus.ACTIVE);

        CampaignItem item = new CampaignItem();
        item.setId(50L);
        item.setCampaign(campaign);
        item.setQuantityNeeded(5);
        item.setQuantityReceived(0);

        Donation donation = new Donation();
        donation.setId(1L);
        donation.setDonationType(DonationType.MATERIAL);
        donation.setCampaign(campaign);
        donation.setCampaignItem(item);
        donation.setDonor(new User());
        donation.getDonor().setEmail("donor@test.com");
        donation.setStatus(DonationStatus.PENDING);
        donation.setQuantity(2);

        when(donationRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(donationRepository.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(campaignItemRepository.save(any(CampaignItem.class))).thenAnswer(inv -> inv.getArgument(0));

        DonationReceipt receipt = new DonationReceipt();
        receipt.setDonation(donation);
        receipt.setReceiptNumber("REC-XYZ");
        receipt.setIssuedAt(LocalDateTime.now());
        when(donationReceiptRepository.findByDonation_Id(1L)).thenReturn(Optional.of(receipt));
        when(donationReceiptRepository.save(any(DonationReceipt.class))).thenAnswer(inv -> inv.getArgument(0));

        DonationResponse stubResponse = new DonationResponse(
            1L,
            DonationType.MATERIAL,
            DonationStatus.CONFIRMED,
            40L,
            "Camp",
            null,
            null,
            null,
            null, // materialDescription
            2, // quantity
            50L,
            null, // notes
            donation.getConfirmedAt(),
            LocalDateTime.now(),
            "REC-XYZ"
        );
        when(donationMapper.toDonationResponse(any(Donation.class), anyString())).thenReturn(stubResponse);

        DonationResponse result = donationService.confirm(1L, "manager@test.com");

        assertEquals(DonationStatus.CONFIRMED, result.status());
        assertEquals(2, item.getQuantityReceived());
        verify(donationReceiptRepository).save(any(DonationReceipt.class));
    }
}

