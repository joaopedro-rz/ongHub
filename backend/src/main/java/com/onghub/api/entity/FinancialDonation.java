package com.onghub.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "financial_donations")
@Getter
@Setter
@NoArgsConstructor
public class FinancialDonation {

    @Id
    @Column(name = "donation_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

    @Column(name = "amount", precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 60)
    private String paymentMethod;

    @Column(name = "proof_url", length = 512)
    private String proofUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;
}

