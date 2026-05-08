package com.onghub.api.service.impl;

import com.onghub.api.dto.dashboard.DashboardApiDtos;
import com.onghub.api.entity.CampaignStatus;
import com.onghub.api.entity.DonationStatus;
import com.onghub.api.entity.DonationType;
import com.onghub.api.entity.Ngo;
import com.onghub.api.entity.User;
import com.onghub.api.entity.VolunteerApplicationStatus;
import com.onghub.api.exception.ResourceNotFoundException;
import com.onghub.api.repository.CampaignRepository;
import com.onghub.api.repository.DonationRepository;
import com.onghub.api.repository.NgoRepository;
import com.onghub.api.repository.UserRepository;
import com.onghub.api.repository.VolunteerApplicationRepository;
import com.onghub.api.security.SecurityUtils;
import com.onghub.api.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final NgoRepository ngoRepository;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final DonationRepository donationRepository;
    private final VolunteerApplicationRepository volunteerApplicationRepository;

    public DashboardServiceImpl(
        NgoRepository ngoRepository,
        UserRepository userRepository,
        CampaignRepository campaignRepository,
        DonationRepository donationRepository,
        VolunteerApplicationRepository volunteerApplicationRepository
    ) {
        this.ngoRepository = ngoRepository;
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
        this.donationRepository = donationRepository;
        this.volunteerApplicationRepository = volunteerApplicationRepository;
    }

    @Override
    public DashboardApiDtos.AdminSummary adminSummary() {
        return new DashboardApiDtos.AdminSummary(
            ngoRepository.count(),
            userRepository.count(),
            campaignRepository.count(),
            donationRepository.count(),
            volunteerApplicationRepository.count()
        );
    }

    @Override
    public DashboardApiDtos.NgoSummary ngoSummary(Long ngoId, String principalEmail) {
        Ngo ngo = ngoRepository.findById(ngoId).orElseThrow(() -> new ResourceNotFoundException("Ngo not found"));
        assertManageNgo(ngo, principalEmail);

        long donationsCount = donationRepository.countByCampaignNgoId(ngoId);
        long activeCampaigns = campaignRepository.countByNgo_IdAndStatus(ngoId, CampaignStatus.ACTIVE);
        long volunteers = volunteerApplicationRepository.countByNgoAndStatus(ngoId, VolunteerApplicationStatus.APPROVED);

        BigDecimal financialTotal = donationRepository.sumFinancialConfirmedForNgo(ngoId, DonationStatus.CONFIRMED, DonationType.FINANCIAL);
        if (financialTotal == null) {
            financialTotal = BigDecimal.ZERO;
        }

        return new DashboardApiDtos.NgoSummary(donationsCount, activeCampaigns, volunteers, financialTotal);
    }

    @Override
    public DashboardApiDtos.DonorSummary donorSummary(String donorEmail) {
        User donor = userRepository.findByEmail(donorEmail.toLowerCase())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        long count = donationRepository.countByDonor_Id(donor.getId());
        BigDecimal financial = donationRepository.sumFinancialForDonor(donor.getId(), DonationStatus.CONFIRMED, DonationType.FINANCIAL);
        if (financial == null) {
            financial = BigDecimal.ZERO;
        }

        long supportedNgos = donationRepository.countDistinctNgosSupportedByDonor(donor.getId());
        return new DashboardApiDtos.DonorSummary(count, financial, supportedNgos);
    }

    private void assertManageNgo(Ngo ngo, String principalEmail) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        if (ngo.getManager() == null || !ngo.getManager().getEmail().equalsIgnoreCase(principalEmail)) {
            throw new ResourceNotFoundException("Ngo not found");
        }
    }
}
