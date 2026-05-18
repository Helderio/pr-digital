package com.ponteshop.dto;

import com.ponteshop.dto.specialrequest.SpecialRequestDto;
import com.ponteshop.enums.CartItemType;
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
public class CartItemDto {
    private UUID id;
    private CartItemType itemType;
    private ProductSummaryDto product;
    private SpecialRequestDto specialRequest;
    private Integer quantity;
    private String selectedVariant;
    private BigDecimal priceAoa;
}
