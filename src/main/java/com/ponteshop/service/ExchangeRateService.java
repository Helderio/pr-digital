package com.ponteshop.service;

import com.ponteshop.dto.ExchangeRateRequest;
import com.ponteshop.entity.ExchangeRate;
import com.ponteshop.exception.ResourceNotFoundException;
import com.ponteshop.repository.ExchangeRateRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRate requireCurrent() {
        return exchangeRateRepository.findFirstByIsCurrentTrue()
            .orElseThrow(() -> new ResourceNotFoundException("Taxa de câmbio atual não definida"));
    }

    public List<ExchangeRate> history() {
        return exchangeRateRepository.findAllByOrderByValidFromDesc();
    }

    @Transactional
    public ExchangeRate create(ExchangeRateRequest req, UUID setBy) {
        exchangeRateRepository.clearCurrent();
        ExchangeRate er = ExchangeRate.builder()
            .rate(req.getRate())
            .marginPct(req.getMarginPct())
            .serviceFeeAoa(req.getServiceFeeAoa())
            .validFrom(req.getValidFrom() == null ? LocalDateTime.now() : req.getValidFrom())
            .setBy(setBy)
            .isCurrent(true)
            .build();
        return exchangeRateRepository.save(er);
    }
}

