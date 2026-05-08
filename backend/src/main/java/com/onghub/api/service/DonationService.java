package com.onghub.api.service;

import com.onghub.api.dto.request.FinancialDonationRequest;
import com.onghub.api.dto.request.MaterialDonationRequest;
import com.onghub.api.dto.response.DonationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DonationService {

    DonationResponse createFinancial(FinancialDonationRequest request, String donorEmail);

    DonationResponse createMaterial(MaterialDonationRequest request, String donorEmail);

    Page<DonationResponse> listMine(Pageable pageable, String donorEmail);

    Page<DonationResponse> listForCampaign(Pageable pageable, Long campaignId, String principalEmail);

    DonationResponse confirm(Long donationId, String principalEmail);

    DonationResponse reject(Long donationId, String principalEmail);

    byte[] downloadReceipt(Long donationId, String principalEmail);
}
