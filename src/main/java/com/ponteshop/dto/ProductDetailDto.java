package com.ponteshop.dto;

import com.ponteshop.enums.ProductStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto {
    private UUID id;
    private StoreDto store;
    private String sourceUrl;
    private String name;
    private String description;
    private String category;
    private List<String> images;
    private List<VariantOptionDto> variants;
    private BigDecimal priceEur;
    private BigDecimal priceAoa;
    private PriceBreakdownDto priceBreakdown;
    private ProductStatus status;
    private boolean isFeatured;
    private LocalDateTime lastSyncedAt;
    private LocalDateTime createdAt;
}

