package com.ponteshop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ponteshop.dto.OrderAdminDto;
import com.ponteshop.dto.OrderDetailDto;
import com.ponteshop.dto.OrderItemSnapshotDto;
import com.ponteshop.dto.TrackingRequest;
import com.ponteshop.dto.UpdateStatusRequest;
import com.ponteshop.entity.Order;
import com.ponteshop.service.AdminOrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {
    private static final TypeReference<List<OrderItemSnapshotDto>> ITEMS_LIST = new TypeReference<>() {};

    private final AdminOrderService adminOrderService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public Page<OrderAdminDto> list(@PageableDefault(size = 20) Pageable pageable) {
        return adminOrderService.list(pageable).map(this::toAdminDto);
    }

    @GetMapping("/{id}")
    public OrderDetailDto get(@PathVariable UUID id) {
        Order o = adminOrderService.require(id);
        return toDetail(o);
    }

    @PatchMapping("/{id}/status")
    public OrderDetailDto updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateStatusRequest req) {
        Order o = adminOrderService.updateStatus(id, req.getStatus());
        return toDetail(o);
    }

    @PatchMapping("/{id}/tracking")
    public OrderDetailDto updateTracking(@PathVariable UUID id, @Valid @RequestBody TrackingRequest req) {
        Order o = adminOrderService.updateTracking(id, req.getTrackingNumber());
        return toDetail(o);
    }

    private OrderAdminDto toAdminDto(Order o) {
        return OrderAdminDto.builder()
            .id(o.getId())
            .orderRef(o.getOrderRef())
            .reference(o.getOrderRef())
            .userId(o.getUser() == null ? null : o.getUser().getId())
            .userEmail(o.getUser() == null ? null : o.getUser().getEmail())
            .email(o.getUser() == null ? null : o.getUser().getEmail())
            .customer(o.getUser() == null ? null : o.getUser().getName())
            .customerName(o.getUser() == null ? null : o.getUser().getName())
            .totalAoa(o.getTotalAoa())
            .profitAoa(o.getProfitAoa())
            .status(o.getStatus())
            .trackingNumber(o.getTrackingNumber())
            .createdAt(o.getCreatedAt())
            .updatedAt(o.getUpdatedAt())
            .build();
    }

    private OrderDetailDto toDetail(Order o) {
        return OrderDetailDto.builder()
            .id(o.getId())
            .orderRef(o.getOrderRef())
            .items(parseItems(o.getItems()))
            .totalEur(o.getTotalEur())
            .totalAoa(o.getTotalAoa())
            .profitAoa(o.getProfitAoa())
            .status(o.getStatus())
            .deliveryAddress(o.getDeliveryAddress())
            .trackingNumber(o.getTrackingNumber())
            .notes(o.getNotes())
            .createdAt(o.getCreatedAt())
            .updatedAt(o.getUpdatedAt())
            .build();
    }

    private List<OrderItemSnapshotDto> parseItems(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, ITEMS_LIST);
        } catch (Exception e) {
            return List.of();
        }
    }
}
