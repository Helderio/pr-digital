package com.ponteshop.dto;

import com.ponteshop.enums.OrderStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingDto {
    private String orderRef;
    private OrderStatus status;
    private String trackingNumber;
    private LocalDateTime updatedAt;
}

