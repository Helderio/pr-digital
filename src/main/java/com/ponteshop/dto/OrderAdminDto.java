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
public class OrderAdminDto {
    private UUID id;
    private String orderRef;
    private String reference;
    private UUID userId;
    private String userEmail;
    private String email;
    private String customer;
    private String customerName;
    private BigDecimal totalAoa;
    private BigDecimal profitAoa;
    private OrderStatus status;
    private String trackingNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
