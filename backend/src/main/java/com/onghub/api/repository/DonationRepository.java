package com.onghub.api.repository;

import com.onghub.api.entity.Donation;
import com.onghub.api.entity.DonationStatus;
import com.onghub.api.entity.DonationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    @EntityGraph(attributePaths = { "campaign", "campaign.ngo", "campaign.ngo.manager", "donor", "campaignItem" })
    @Override
    Optional<Donation> findById(Long id);

    Page<Donation> findByDonor_Id(Long donorId, Pageable pageable);

    long countByDonor_Id(Long donorId);

    Page<Donation> findByCampaign_Id(Long campaignId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM Donation d JOIN d.campaign c WHERE c.ngo.id = :ngoId")
    long countByCampaignNgoId(@Param("ngoId") Long ngoId);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.donor.id = :uid AND d.status = :st AND d.donationType = :dtype")
    BigDecimal sumFinancialForDonor(@Param("uid") Long uid, @Param("st") DonationStatus st, @Param("dtype") DonationType dtype);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d JOIN d.campaign c WHERE c.ngo.id = :ngoId AND d.status = :st AND d.donationType = :dtype")
    BigDecimal sumFinancialConfirmedForNgo(@Param("ngoId") Long ngoId, @Param("st") DonationStatus st, @Param("dtype") DonationType dtype);

    @Query("SELECT COUNT(DISTINCT c.ngo.id) FROM Donation d JOIN d.campaign c WHERE d.donor.id = :donorId")
    long countDistinctNgosSupportedByDonor(@Param("donorId") Long donorId);

    @Query("SELECT d FROM Donation d JOIN FETCH d.donor JOIN FETCH d.campaign c JOIN FETCH c.ngo n WHERE n.id = :ngoId ORDER BY d.createdAt DESC")
    List<Donation> findAllForNgoExport(@Param("ngoId") Long ngoId);

    @EntityGraph(attributePaths = { "donor", "campaign", "campaign.ngo", "campaignItem" })
    List<Donation> findByCampaign_IdOrderByCreatedAtDesc(Long campaignId);
}
