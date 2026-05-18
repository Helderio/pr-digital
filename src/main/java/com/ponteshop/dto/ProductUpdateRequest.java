package com.ponteshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {
    @NotBlank
    @Size(max = 300)
    private String name;

    private String description;

    private String category;

    private List<String> images;

    private List<VariantOptionDto> variants;

    private BigDecimal priceEur;

    private boolean isFeatured;
}

