package com.onghub.api.repository;

import com.onghub.api.entity.MaterialDonation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialDonationRepository extends JpaRepository<MaterialDonation, Long> {

    Optional<MaterialDonation> findByDonation_Id(Long donationId);
}

