package com.ponteshop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ponteshop.dto.CheckoutRequest;
import com.ponteshop.dto.OrderConfirmationDto;
import com.ponteshop.dto.OrderDetailDto;
import com.ponteshop.dto.OrderItemSnapshotDto;
import com.ponteshop.dto.OrderSummaryDto;
import com.ponteshop.dto.PriceBreakdownDto;
import com.ponteshop.dto.TrackingDto;
import com.ponteshop.entity.CartItem;
import com.ponteshop.entity.Order;
import com.ponteshop.entity.Product;
import com.ponteshop.entity.SpecialRequest;
import com.ponteshop.enums.CartItemType;
import com.ponteshop.enums.OrderStatus;
import com.ponteshop.enums.SpecialRequestStatus;
import com.ponteshop.exception.ForbiddenException;
import com.ponteshop.exception.ResourceNotFoundException;
import com.ponteshop.repository.CartItemRepository;
import com.ponteshop.repository.OrderRepository;
import com.ponteshop.repository.SpecialRequestRepository;
import com.ponteshop.repository.UserRepository;
import com.ponteshop.util.JsonMapper;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final TypeReference<List<OrderItemSnapshotDto>> ITEMS_LIST = new TypeReference<>() {};

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SpecialRequestRepository specialRequestRepository;
    private final JsonMapper jsonMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderConfirmationDto checkout(UUID userId, CheckoutRequest req) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByAddedAtDesc(userId);
        if (cartItems.isEmpty()) {
            throw new ForbiddenException("Carrinho vazio");
        }

        List<OrderItemSnapshotDto> items = cartItems.stream().map(this::toSnapshot).toList();
        BigDecimal totalEur = items.stream()
            .map(i -> safe(i.getPriceEur()).multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAoa = items.stream()
            .map(i -> safe(i.getPriceAoa()).multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal baseAoa = cartItems.stream()
            .map(ci -> {
                PriceBreakdownDto breakdown;
                if (ci.getItemType() == CartItemType.SPECIAL_REQUEST && ci.getSpecialRequest() != null) {
                    breakdown = jsonMapper.toBreakdown(ci.getSpecialRequest().getPriceBreakdown());
                } else if (ci.getProduct() != null) {
                    breakdown = jsonMapper.toBreakdown(ci.getProduct().getPriceBreakdown());
                } else {
                    breakdown = null;
                }
                return safe(breakdown == null ? null : breakdown.getProductAoa())
                    .multiply(BigDecimal.valueOf(ci.getQuantity()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal profitAoa = totalAoa.subtract(baseAoa);

        String itemsJson = jsonMapper.toJson(items);
        String deliveryJson = jsonMapper.toJson(req.getDeliveryAddress());

        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));

        Order order = Order.builder()
            .orderRef(generateOrderRef())
            .user(user)
            .items(itemsJson)
            .totalEur(totalEur)
            .totalAoa(totalAoa)
            .profitAoa(profitAoa)
            .status(OrderStatus.PENDING)
            .deliveryAddress(deliveryJson)
            .trackingNumber(null)
            .notes(req.getNotes())
            .build();

        Order saved = orderRepository.save(order);

        for (CartItem ci : cartItems) {
            if (ci.getItemType() == CartItemType.SPECIAL_REQUEST && ci.getSpecialRequest() != null) {
                SpecialRequest sr = ci.getSpecialRequest();
                sr.setStatus(SpecialRequestStatus.ORDERED);
                sr.setCartItemId(null);
                specialRequestRepository.save(sr);
            }
        }

        cartItemRepository.deleteByUserId(userId);

        return OrderConfirmationDto.builder()
            .id(saved.getId())
            .orderRef(saved.getOrderRef())
            .totalAoa(saved.getTotalAoa())
            .status(saved.getStatus())
            .build();
    }

    public List<OrderSummaryDto> myOrders(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(o -> OrderSummaryDto.builder()
                .id(o.getId())
                .orderRef(o.getOrderRef())
                .totalAoa(o.getTotalAoa())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .build())
            .toList();
    }

    public OrderDetailDto myOrder(UUID userId, UUID orderId) {
        Order o = orderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Encomenda não encontrada"));
        return toDetail(o);
    }

    public TrackingDto trackPublic(String orderRef) {
        Order o = orderRepository.findByOrderRef(orderRef)
            .orElseThrow(() -> new ResourceNotFoundException("Encomenda não encontrada"));
        return TrackingDto.builder()
            .orderRef(o.getOrderRef())
            .status(o.getStatus())
            .trackingNumber(o.getTrackingNumber())
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
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, ITEMS_LIST);
        } catch (Exception e) {
            return List.of();
        }
    }

    private OrderItemSnapshotDto toSnapshot(CartItem cartItem) {
        if (cartItem.getItemType() == CartItemType.SPECIAL_REQUEST && cartItem.getSpecialRequest() != null) {
            SpecialRequest sr = cartItem.getSpecialRequest();
            return OrderItemSnapshotDto.builder()
                .itemType(CartItemType.SPECIAL_REQUEST.name())
                .productId(null)
                .specialRequestId(sr.getId())
                .name(sr.getDetectedName())
                .quantity(cartItem.getQuantity())
                .selectedVariant(cartItem.getSelectedVariant())
                .priceEur(sr.getDetectedPriceEur())
                .priceAoa(cartItem.getPriceAoaSnapshot())
                .storeId(sr.getStore() == null ? null : sr.getStore().getId())
                .storeSlug(sr.getStore() == null ? null : sr.getStore().getSlug())
                .sourceUrl(sr.getSourceUrl())
                .build();
        }

        Product p = cartItem.getProduct();
        if (p == null) {
            throw new IllegalStateException("Item de carrinho CATALOG sem produto");
        }
        return OrderItemSnapshotDto.builder()
            .itemType(CartItemType.CATALOG.name())
            .productId(p.getId())
            .specialRequestId(null)
            .name(p.getName())
            .quantity(cartItem.getQuantity())
            .selectedVariant(cartItem.getSelectedVariant())
            .priceEur(p.getPriceEur())
            .priceAoa(cartItem.getPriceAoaSnapshot())
            .storeId(p.getStore() == null ? null : p.getStore().getId())
            .storeSlug(p.getStore() == null ? null : p.getStore().getSlug())
            .sourceUrl(p.getSourceUrl())
            .build();
    }

    private String generateOrderRef() {
        int year = LocalDate.now().getYear();
        long count = orderRepository.count() + 1;
        return String.format("PS-%d-%05d", year, count);
    }

    private static BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
