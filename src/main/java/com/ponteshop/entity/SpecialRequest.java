package com.ponteshop.entity;

import com.ponteshop.enums.SpecialRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "special_requests")
public class SpecialRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "source_url", nullable = false, columnDefinition = "text")
    private String sourceUrl;

    @Column(name = "detected_name", length = 255)
    private String detectedName;

    @Column(name = "detected_description", columnDefinition = "text")
    private String detectedDescription;

    @Column(name = "detected_images", columnDefinition = "text")
    private String detectedImages;

    @Column(name = "detected_variants", columnDefinition = "text")
    private String detectedVariants;

    @Column(name = "detected_price_eur", precision = 14, scale = 2)
    private BigDecimal detectedPriceEur;

    @Column(name = "calculated_price_aoa", precision = 18, scale = 2)
    private BigDecimal calculatedPriceAoa;

    @Column(name = "price_breakdown", columnDefinition = "text")
    private String priceBreakdown;

    @Column(name = "selected_variant", columnDefinition = "text")
    private String selectedVariant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SpecialRequestStatus status;

    @Column(name = "cart_item_id")
    private UUID cartItemId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
