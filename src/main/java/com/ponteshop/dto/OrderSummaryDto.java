package com.ponteshop.dto;

import com.ponteshop.enums.OrderStatus;
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
public class OrderSummaryDto {
    private UUID id;
    private String orderRef;
    private BigDecimal totalAoa;
    private OrderStatus status;
    private LocalDateTime createdAt;
}

