package com.ponteshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal rate;

    @Column(name = "margin_pct", nullable = false, precision = 6, scale = 2)
    private BigDecimal marginPct;

    @Column(name = "service_fee_aoa", nullable = false, precision = 18, scale = 2)
    private BigDecimal serviceFeeAoa;

    @Column(name = "set_by")
    private UUID setBy;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent;
}

