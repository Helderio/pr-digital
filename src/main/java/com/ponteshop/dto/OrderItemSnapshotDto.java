package com.ponteshop.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemSnapshotDto {
    private String itemType;
    private UUID productId;
    private UUID specialRequestId;
    private String name;
    private Integer quantity;
    private String selectedVariant;
    private BigDecimal priceEur;
    private BigDecimal priceAoa;
    private Integer storeId;
    private String storeSlug;
    private String sourceUrl;
}
