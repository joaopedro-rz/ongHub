package com.onghub.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "material_donations")
@Getter
@Setter
@NoArgsConstructor
public class MaterialDonation {

    @Id
    @Column(name = "donation_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @Column(name = "material_description", length = 500)
    private String materialDescription;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_item_id")
    private CampaignItem campaignItem;

    @Column(name = "proof_url", length = 512)
    private String proofUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;
}

