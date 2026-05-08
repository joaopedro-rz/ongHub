package com.onghub.api.service;

import com.onghub.api.entity.Donation;
import com.onghub.api.entity.Ngo;
import com.onghub.api.exception.ResourceNotFoundException;
import com.onghub.api.repository.DonationRepository;
import com.onghub.api.repository.NgoRepository;
import com.onghub.api.security.SecurityUtils;
import com.onghub.api.util.TransparencyCsvWriter;
import com.onghub.api.util.TransparencyPdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TransparencyReportService {

    private final DonationRepository donationRepository;
    private final NgoRepository ngoRepository;

    public TransparencyReportService(DonationRepository donationRepository, NgoRepository ngoRepository) {
        this.donationRepository = donationRepository;
        this.ngoRepository = ngoRepository;
    }

    public byte[] csv(Long ngoId, String principalEmail) {
        return TransparencyCsvWriter.toBytes(loadDonations(ngoId, principalEmail));
    }

    public byte[] pdf(Long ngoId, String principalEmail) {
        List<Donation> rows = loadDonations(ngoId, principalEmail);
        Ngo ngo = ngoRepository.findById(ngoId).orElseThrow(() -> new ResourceNotFoundException("Ngo not found"));
        return TransparencyPdfWriter.build(ngo.getName(), rows);
    }

    private List<Donation> loadDonations(Long ngoId, String principalEmail) {
        Ngo ngo = ngoRepository.findById(ngoId).orElseThrow(() -> new ResourceNotFoundException("Ngo not found"));
        assertManage(ngo, principalEmail);
        return donationRepository.findAllForNgoExport(ngoId);
    }

    private void assertManage(Ngo ngo, String principalEmail) {
        if (SecurityUtils.isAdmin()) {
            return;
        }
        if (ngo.getManager() == null || !ngo.getManager().getEmail().equalsIgnoreCase(principalEmail)) {
            throw new ResourceNotFoundException("Ngo not found");
        }
    }
}
