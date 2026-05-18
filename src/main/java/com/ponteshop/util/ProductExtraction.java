package com.ponteshop.util;

import com.ponteshop.dto.VariantOptionDto;
import java.math.BigDecimal;
import java.util.List;

public record ProductExtraction(
    String name,
    BigDecimal priceEur,
    String description,
    String category,
    String brand,
    List<String> images,
    List<VariantOptionDto> variants
) {
}
