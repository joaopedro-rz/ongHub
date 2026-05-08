package com.onghub.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_items")
@Getter
@Setter
@NoArgsConstructor
public class CampaignItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(length = 120)
    private String category;

    @Column(name = "quantity_needed", nullable = false)
    private int quantityNeeded;

    @Column(name = "quantity_received", nullable = false)
    private int quantityReceived;

    @Column(length = 40)
    private String unit;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
