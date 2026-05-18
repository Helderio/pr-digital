package com.ponteshop.dto;

import java.math.BigDecimal;
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
public class ProductSummaryDto {
    private UUID id;
    private StoreDto store;
    private String name;
    private String category;
    private List<String> images;
    private BigDecimal priceEur;
    private BigDecimal priceAoa;
}

