package com.ponteshop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ponteshop.dto.OrderItemSnapshotDto;
import com.ponteshop.entity.Order;
import com.ponteshop.enums.OrderStatus;
import com.ponteshop.exception.ResourceNotFoundException;
import com.ponteshop.repository.OrderRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private static final TypeReference<List<OrderItemSnapshotDto>> ITEMS_LIST = new TypeReference<>() {};

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public Page<Order> list(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order require(UUID id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Encomenda não encontrada"));
    }

    @Transactional
    public Order updateStatus(UUID id, OrderStatus status) {
        Order o = require(id);
        o.setStatus(status);
        return orderRepository.save(o);
    }

    @Transactional
    public Order updateTracking(UUID id, String trackingNumber) {
        Order o = require(id);
        o.setTrackingNumber(trackingNumber);
        return orderRepository.save(o);
    }

    public List<OrderItemSnapshotDto> parseItems(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, ITEMS_LIST);
        } catch (Exception e) {
            return List.of();
        }
    }
}

