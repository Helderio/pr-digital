package com.ponteshop.util;

import com.ponteshop.dto.PriceBreakdownDto;
import com.ponteshop.entity.ExchangeRate;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PriceCalculator {
    private PriceCalculator() {
    }

    public static PriceBreakdownDto calculate(ExchangeRate current, BigDecimal priceEur) {
        if (current == null) throw new IllegalArgumentException("ExchangeRate current is required");
        if (priceEur == null) throw new IllegalArgumentException("priceEur is required");

        BigDecimal rate = current.getRate();
        BigDecimal baseAoa = priceEur.multiply(rate);
        BigDecimal exchangeFee = baseAoa.multiply(current.getMarginPct().divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP));
        BigDecimal shippingAoa = new BigDecimal("35")
            .multiply(rate)
            .multiply(new BigDecimal("1.12"));

        BigDecimal total = baseAoa
            .add(exchangeFee)
            .add(current.getServiceFeeAoa())
            .add(shippingAoa);

        return PriceBreakdownDto.builder()
            .productAoa(scale(baseAoa))
            .exchangeFee(scale(exchangeFee))
            .serviceFeeAoa(scale(current.getServiceFeeAoa()))
            .shippingAoa(scale(shippingAoa))
            .total(scale(total))
            .build();
    }

    private static BigDecimal scale(BigDecimal v) {
        if (v == null) return null;
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}

