package com.ponteshop.entity;

import com.ponteshop.enums.ImportedBy;
import com.ponteshop.enums.ProductStatus;
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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "source_url", nullable = false, length = 1000)
    private String sourceUrl;

    @Column(nullable = false, length = 300)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 80)
    private String category;

    @Column(columnDefinition = "text")
    private String images;

    @Column(columnDefinition = "text")
    private String variants;

    @Column(name = "price_eur", precision = 14, scale = 2)
    private BigDecimal priceEur;

    @Column(name = "price_aoa", precision = 18, scale = 2)
    private BigDecimal priceAoa;

    @Column(name = "price_breakdown", columnDefinition = "text")
    private String priceBreakdown;

    @Enumerated(EnumType.STRING)
    @Column(name = "imported_by", nullable = false, length = 30)
    private ImportedBy importedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductStatus status;

    @Column(name = "is_featured", nullable = false)
    private boolean isFeatured = false;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
