package com.ponteshop.dto;

import com.ponteshop.enums.CartItemType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    @NotNull
    private CartItemType itemType;

    private UUID productId;

    private UUID specialRequestId;

    @NotNull
    @Min(1)
    private Integer quantity;

    private String selectedVariant;

    @AssertTrue(message = "itemType requer productId (CATALOG) ou specialRequestId (SPECIAL_REQUEST)")
    public boolean isConsistentPayload() {
        if (itemType == null) {
            return false;
        }
        if (itemType == CartItemType.CATALOG) {
            return productId != null && specialRequestId == null;
        }
        if (itemType == CartItemType.SPECIAL_REQUEST) {
            return specialRequestId != null && productId == null;
        }
        return false;
    }
}
