package com.ponteshop.service;

import com.ponteshop.dto.ProductUpdateRequest;
import com.ponteshop.entity.Product;
import com.ponteshop.enums.ProductStatus;
import com.ponteshop.exception.ResourceNotFoundException;
import com.ponteshop.repository.ProductRepository;
import com.ponteshop.repository.ProductSpecifications;
import com.ponteshop.util.JsonMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminProductService {
    private final ProductRepository productRepository;
    private final PriceCalculatorService priceCalculatorService;
    private final JsonMapper jsonMapper;

    public Page<Product> list(String store, String category, String search, ProductStatus status, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecifications.storeSlug(store))
            .and(ProductSpecifications.category(category))
            .and(ProductSpecifications.search(search));
        if (status != null) spec = spec.and(ProductSpecifications.status(status));
        return productRepository.findAll(spec, pageable);
    }

    public Product require(UUID id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }

    @Transactional
    public Product publish(UUID id) {
        Product p = require(id);
        p.setStatus(ProductStatus.ACTIVE);
        return productRepository.save(p);
    }

    @Transactional
    public Product unpublish(UUID id) {
        Product p = require(id);
        p.setStatus(ProductStatus.INACTIVE);
        return productRepository.save(p);
    }

    @Transactional
    public Product update(UUID id, ProductUpdateRequest req) {
        Product p = require(id);
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setCategory(req.getCategory());
        p.setImages(jsonMapper.fromStringList(req.getImages()));
        p.setVariants(jsonMapper.fromVariantList(req.getVariants()));
        p.setFeatured(req.isFeatured());

        if (req.getPriceEur() != null) {
            p.setPriceEur(req.getPriceEur());
            PriceCalculatorService.PriceCalculationResult calc = priceCalculatorService.calculatePriceAoa(req.getPriceEur());
            p.setPriceAoa(calc.totalAoa());
            p.setPriceBreakdown(calc.breakdownJson());
            p.setLastSyncedAt(LocalDateTime.now());
        }
        return productRepository.save(p);
    }
}

