package com.ponteshop.controller;

import com.ponteshop.enums.OrderStatus;
import com.ponteshop.enums.SpecialRequestStatus;
import com.ponteshop.repository.OrderRepository;
import com.ponteshop.repository.ProductRepository;
import com.ponteshop.repository.SpecialRequestRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SpecialRequestRepository specialRequestRepository;

    @GetMapping("/kpis")
    public Map<String, Object> kpis() {
        BigDecimal revenueAoa = orderRepository.sumTotalAoaByStatusNot(OrderStatus.CANCELLED.name());
        BigDecimal profitAoa = orderRepository.sumProfitAoaByStatusNot(OrderStatus.CANCELLED.name());
        long pendingSpecialRequests = specialRequestRepository.countByStatusIn(List.of(
            SpecialRequestStatus.ANALYSING,
            SpecialRequestStatus.PRICED,
            SpecialRequestStatus.IN_CART,
            SpecialRequestStatus.ORDERED,
            SpecialRequestStatus.BUYING_PT
        ));

        return Map.of(
            "totalOrders", orderRepository.count(),
            "pendingSpecialRequests", pendingSpecialRequests,
            "estimatedProfit", profitAoa == null ? BigDecimal.ZERO : profitAoa,
            "orders", orderRepository.count(),
            "products", productRepository.count(),
            "specialRequests", specialRequestRepository.count(),
            "revenueAoa", revenueAoa == null ? BigDecimal.ZERO : revenueAoa,
            "profitAoa", profitAoa == null ? BigDecimal.ZERO : profitAoa
        );
    }
}
