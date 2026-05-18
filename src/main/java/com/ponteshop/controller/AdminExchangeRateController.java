package com.ponteshop.controller;

import com.ponteshop.dto.ExchangeRateDto;
import com.ponteshop.dto.ExchangeRateRequest;
import com.ponteshop.dto.mapper.ExchangeRateMapper;
import com.ponteshop.security.SecurityUtil;
import com.ponteshop.service.ExchangeRateService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/exchange-rates")
public class AdminExchangeRateController {
    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateMapper exchangeRateMapper;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ExchangeRateDto create(@Valid @RequestBody ExchangeRateRequest req) {
        return exchangeRateMapper.toDto(exchangeRateService.create(req, securityUtil.requireCurrentUserId()));
    }

    @GetMapping
    public List<ExchangeRateDto> history() {
        return exchangeRateService.history().stream().map(exchangeRateMapper::toDto).toList();
    }

    @GetMapping("/current")
    public ExchangeRateDto current() {
        return exchangeRateMapper.toDto(exchangeRateService.requireCurrent());
    }
}

