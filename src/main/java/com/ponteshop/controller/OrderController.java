package com.ponteshop.controller;

import com.ponteshop.dto.CheckoutRequest;
import com.ponteshop.dto.OrderConfirmationDto;
import com.ponteshop.dto.OrderDetailDto;
import com.ponteshop.dto.OrderSummaryDto;
import com.ponteshop.dto.TrackingDto;
import com.ponteshop.security.SecurityUtil;
import com.ponteshop.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final SecurityUtil securityUtil;

    @PostMapping("/checkout")
    public OrderConfirmationDto checkout(@Valid @RequestBody CheckoutRequest req) {
        return orderService.checkout(securityUtil.requireCurrentUserId(), req);
    }

    @GetMapping
    public List<OrderSummaryDto> listMine() {
        return orderService.myOrders(securityUtil.requireCurrentUserId());
    }

    @GetMapping("/{id}")
    public OrderDetailDto mine(@PathVariable UUID id) {
        return orderService.myOrder(securityUtil.requireCurrentUserId(), id);
    }

    @GetMapping("/{orderRef}/track")
    public TrackingDto track(@PathVariable String orderRef) {
        return orderService.trackPublic(orderRef);
    }
}

