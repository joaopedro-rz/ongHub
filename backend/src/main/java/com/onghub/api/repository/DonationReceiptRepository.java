package com.onghub.api.repository;

import com.onghub.api.entity.DonationReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DonationReceiptRepository extends JpaRepository<DonationReceipt, Long> {
    Optional<DonationReceipt> findByDonation_Id(Long donationId);
}
