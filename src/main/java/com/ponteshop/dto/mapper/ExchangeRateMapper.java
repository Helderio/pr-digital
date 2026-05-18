package com.ponteshop.dto.mapper;

import com.ponteshop.dto.ExchangeRateDto;
import com.ponteshop.entity.ExchangeRate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExchangeRateMapper {
    ExchangeRateDto toDto(ExchangeRate exchangeRate);
}

