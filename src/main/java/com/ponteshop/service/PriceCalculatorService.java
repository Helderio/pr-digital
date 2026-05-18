package com.ponteshop.service;

import com.ponteshop.dto.PriceBreakdownDto;
import com.ponteshop.entity.ExchangeRate;
import com.ponteshop.repository.ExchangeRateRepository;
import com.ponteshop.util.JsonMapper;
import com.ponteshop.util.PriceCalculator;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceCalculatorService {
    private final ExchangeRateRepository exchangeRateRepository;
    private final JsonMapper jsonMapper;

    public PriceCalculationResult calculatePriceAoa(BigDecimal priceEur) {
        ExchangeRate current = exchangeRateRepository.findFirstByIsCurrentTrue()
            .orElseThrow(() -> new IllegalStateException("Exchange rate current not set"));
        PriceBreakdownDto breakdown = PriceCalculator.calculate(current, priceEur);
        String breakdownJson = jsonMapper.fromBreakdown(breakdown);
        return new PriceCalculationResult(breakdown.getTotal(), breakdown, breakdownJson);
    }

    public record PriceCalculationResult(BigDecimal totalAoa, PriceBreakdownDto breakdown, String breakdownJson) {
    }
}

