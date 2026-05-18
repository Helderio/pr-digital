package com.ponteshop.repository;

import com.ponteshop.entity.CartItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    @EntityGraph(attributePaths = {"product", "product.store", "specialRequest", "specialRequest.store"})
    List<CartItem> findByUserIdOrderByAddedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {"product", "product.store", "specialRequest", "specialRequest.store"})
    Optional<CartItem> findByIdAndUserId(UUID id, UUID userId);

    Optional<CartItem> findFirstByUserIdAndProductIdAndSelectedVariant(UUID userId, UUID productId, String selectedVariant);

    Optional<CartItem> findFirstByUserIdAndSpecialRequestIdAndSelectedVariant(UUID userId, UUID specialRequestId, String selectedVariant);

    void deleteByUserId(UUID userId);
}
