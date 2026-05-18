package com.ponteshop.repository;

import com.ponteshop.entity.Product;
import com.ponteshop.enums.ProductStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    List<Product> findTop20ByStatusAndIsFeaturedTrueOrderByCreatedAtDesc(ProductStatus status);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
}

