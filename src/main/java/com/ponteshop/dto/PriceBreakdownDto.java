package com.ponteshop.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceBreakdownDto {
    private BigDecimal productAoa;
    private BigDecimal exchangeFee;
    private BigDecimal serviceFeeAoa;
    private BigDecimal shippingAoa;
    private BigDecimal total;
}

