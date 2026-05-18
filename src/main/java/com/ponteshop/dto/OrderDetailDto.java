package com.ponteshop.dto;

import com.ponteshop.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    private UUID id;
    private String orderRef;
    private List<OrderItemSnapshotDto> items;
    private BigDecimal totalEur;
    private BigDecimal totalAoa;
    private BigDecimal profitAoa;
    private OrderStatus status;
    private String deliveryAddress;
    private String trackingNumber;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

