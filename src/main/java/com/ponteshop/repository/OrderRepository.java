package com.ponteshop.repository;

import com.ponteshop.entity.Order;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Order> findByIdAndUserId(UUID id, UUID userId);

    Optional<Order> findByOrderRef(String orderRef);

    @Query(value = "select coalesce(sum(total_aoa), 0) from orders where status <> :status", nativeQuery = true)
    BigDecimal sumTotalAoaByStatusNot(@Param("status") String status);

    @Query(value = "select coalesce(sum(profit_aoa), 0) from orders where status <> :status", nativeQuery = true)
    BigDecimal sumProfitAoaByStatusNot(@Param("status") String status);
}
