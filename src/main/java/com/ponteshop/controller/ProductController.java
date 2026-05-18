package com.ponteshop.controller;

import com.ponteshop.dto.ProductDetailDto;
import com.ponteshop.dto.ProductSummaryDto;
import com.ponteshop.dto.mapper.ProductMapper;
import com.ponteshop.service.ProductService;
import com.ponteshop.util.JsonMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final JsonMapper jsonMapper;

    @GetMapping
    public Page<ProductSummaryDto> list(
        @RequestParam(required = false) String store,
        @RequestParam(required = false) String category,
        @RequestParam(required = false, name = "search") String search,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return productService.listPublic(store, category, search, pageable)
            .map(p -> productMapper.toSummary(p, jsonMapper));
    }

    @GetMapping("/{id}")
    public ProductDetailDto get(@PathVariable UUID id) {
        return productMapper.toDetail(productService.getPublic(id), jsonMapper);
    }

    @GetMapping("/featured")
    public List<ProductSummaryDto> featured() {
        return productService.featured().stream()
            .map(p -> productMapper.toSummary(p, jsonMapper))
            .toList();
    }
}

