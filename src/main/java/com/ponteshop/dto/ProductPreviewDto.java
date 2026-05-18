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
public class ProductPreviewDto {
    private UUID id;
    private Integer storeId;
    private String sourceUrl;
    private String name;
    private BigDecimal priceEur;
    private BigDecimal priceAoa;
    private String description;
    private String category;
    private List<String> images;
    private List<VariantOptionDto> variants;
    private PriceBreakdownDto priceBreakdown;
}

