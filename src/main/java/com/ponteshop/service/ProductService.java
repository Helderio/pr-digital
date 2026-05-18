package com.ponteshop.service;

import com.ponteshop.entity.Product;
import com.ponteshop.enums.ProductStatus;
import com.ponteshop.exception.ResourceNotFoundException;
import com.ponteshop.repository.ProductRepository;
import com.ponteshop.repository.ProductSpecifications;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<Product> listPublic(String store, String category, String search, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecifications.status(ProductStatus.ACTIVE))
            .and(ProductSpecifications.storeSlug(store))
            .and(ProductSpecifications.category(category))
            .and(ProductSpecifications.search(search));
        return productRepository.findAll(spec, pageable);
    }

    public Product getPublic(UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ResourceNotFoundException("Produto não encontrado");
        }
        return product;
    }

    public List<Product> featured() {
        return productRepository.findTop20ByStatusAndIsFeaturedTrueOrderByCreatedAtDesc(ProductStatus.ACTIVE);
    }
}

