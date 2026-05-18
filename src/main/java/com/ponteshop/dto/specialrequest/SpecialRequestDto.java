package com.ponteshop.dto.specialrequest;

import com.ponteshop.dto.PriceBreakdownDto;
import com.ponteshop.dto.VariantOptionDto;
import com.ponteshop.enums.SpecialRequestStatus;
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
public class SpecialRequestDto {
    private UUID id;
    private Integer storeId;
    private String storeSlug;
    private String sourceUrl;
    private String detectedName;
    private String detectedDescription;
    private List<String> detectedImages;
    private List<VariantOptionDto> detectedVariants;
    private BigDecimal detectedPriceEur;
    private BigDecimal calculatedPriceAoa;
    private PriceBreakdownDto priceBreakdown;
    private String selectedVariant;
    private SpecialRequestStatus status;
    private UUID cartItemId;
    private UUID productId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
