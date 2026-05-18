package com.ponteshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ponteshop.entity.ExchangeRate;
import com.ponteshop.repository.ExchangeRateRepository;
import com.ponteshop.util.JsonMapper;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceCalculatorServiceTest {
    @Test
    void calculatePriceAoa_calculatesWithBreakdown() {
        ExchangeRateRepository repo = Mockito.mock(ExchangeRateRepository.class);
        JsonMapper jsonMapper = new JsonMapper(new ObjectMapper());
        PriceCalculatorService service = new PriceCalculatorService(repo, jsonMapper);

        ExchangeRate rate = ExchangeRate.builder()
            .rate(new BigDecimal("1020.00"))
            .marginPct(new BigDecimal("7.00"))
            .serviceFeeAoa(new BigDecimal("5000.00"))
            .isCurrent(true)
            .build();
        Mockito.when(repo.findFirstByIsCurrentTrue()).thenReturn(Optional.of(rate));

        var result = service.calculatePriceAoa(new BigDecimal("69.95"));

        assertThat(result.totalAoa()).isEqualByComparingTo("121327.43");
        assertThat(result.breakdown().getProductAoa()).isEqualByComparingTo("71349.00");
        assertThat(result.breakdown().getExchangeFee()).isEqualByComparingTo("4994.43");
        assertThat(result.breakdown().getShippingAoa()).isEqualByComparingTo("39984.00");
        assertThat(result.breakdown().getServiceFeeAoa()).isEqualByComparingTo("5000.00");
        assertThat(result.breakdownJson()).contains("121327.43");
    }

    @Test
    void calculatePriceAoa_throwsWhenNoCurrentExchangeRate() {
        ExchangeRateRepository repo = Mockito.mock(ExchangeRateRepository.class);
        JsonMapper jsonMapper = new JsonMapper(new ObjectMapper());
        PriceCalculatorService service = new PriceCalculatorService(repo, jsonMapper);
        Mockito.when(repo.findFirstByIsCurrentTrue()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.calculatePriceAoa(new BigDecimal("10.00")))
            .isInstanceOf(IllegalStateException.class);
    }
}

