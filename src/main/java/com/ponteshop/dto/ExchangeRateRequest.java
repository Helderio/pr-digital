package com.ponteshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateRequest {
    @NotNull
    @DecimalMin("1.0")
    private BigDecimal rate;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal marginPct;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal serviceFeeAoa;

    @NotNull
    private LocalDateTime validFrom;
}

