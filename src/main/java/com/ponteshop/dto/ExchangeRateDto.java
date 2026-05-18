package com.ponteshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {
    private Integer id;
    private BigDecimal rate;
    private BigDecimal marginPct;
    private BigDecimal serviceFeeAoa;
    private UUID setBy;
    private LocalDateTime validFrom;
    private boolean isCurrent;
}

