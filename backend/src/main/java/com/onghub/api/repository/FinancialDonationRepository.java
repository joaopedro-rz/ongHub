package com.onghub.api.repository;

import com.onghub.api.entity.FinancialDonation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinancialDonationRepository extends JpaRepository<FinancialDonation, Long> {

    Optional<FinancialDonation> findByDonation_Id(Long donationId);
}

