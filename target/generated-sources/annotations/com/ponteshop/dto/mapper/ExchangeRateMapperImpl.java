package com.ponteshop.dto.mapper;

import com.ponteshop.dto.ExchangeRateDto;
import com.ponteshop.entity.ExchangeRate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-18T10:26:13+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2 (Arch Linux)"
)
@Component
public class ExchangeRateMapperImpl implements ExchangeRateMapper {

    @Override
    public ExchangeRateDto toDto(ExchangeRate exchangeRate) {
        if ( exchangeRate == null ) {
            return null;
        }

        ExchangeRateDto.ExchangeRateDtoBuilder exchangeRateDto = ExchangeRateDto.builder();

        exchangeRateDto.id( exchangeRate.getId() );
        exchangeRateDto.rate( exchangeRate.getRate() );
        exchangeRateDto.marginPct( exchangeRate.getMarginPct() );
        exchangeRateDto.serviceFeeAoa( exchangeRate.getServiceFeeAoa() );
        exchangeRateDto.validFrom( exchangeRate.getValidFrom() );

        return exchangeRateDto.build();
    }
}
