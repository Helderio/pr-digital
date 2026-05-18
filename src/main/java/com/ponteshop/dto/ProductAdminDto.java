package com.ponteshop.dto;

import com.ponteshop.enums.ImportedBy;
import com.ponteshop.enums.ProductStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminDto {
    private UUID id;
    private StoreDto store;
    private String sourceUrl;
    private String name;
    private String category;
    private BigDecimal priceEur;
    private BigDecimal priceAoa;
    private ImportedBy importedBy;
    private ProductStatus status;
    private boolean isFeatured;
    private LocalDateTime lastSyncedAt;
    private LocalDateTime createdAt;
}

