package com.ponteshop.controller;

import com.ponteshop.dto.ImportUrlRequest;
import com.ponteshop.dto.ProductAdminDto;
import com.ponteshop.dto.ProductDetailDto;
import com.ponteshop.dto.ProductPreviewDto;
import com.ponteshop.dto.ProductUpdateRequest;
import com.ponteshop.dto.mapper.ProductMapper;
import com.ponteshop.enums.ProductStatus;
import com.ponteshop.service.AdminProductService;
import com.ponteshop.service.ProductImportService;
import com.ponteshop.util.JsonMapper;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/products")
public class AdminProductController {
    private final ProductImportService productImportService;
    private final AdminProductService adminProductService;
    private final ProductMapper productMapper;
    private final JsonMapper jsonMapper;

    @PostMapping("/import-url")
    public ProductPreviewDto importUrl(@Valid @RequestBody ImportUrlRequest req) {
        return productImportService.importFromUrl(req.getUrl(), req.getStoreId());
    }

    @GetMapping
    public Page<ProductAdminDto> list(
        @RequestParam(required = false) String store,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) ProductStatus status,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return adminProductService.list(store, category, search, status, pageable)
            .map(productMapper::toAdmin);
    }

    @PatchMapping("/{id}/publish")
    public ProductDetailDto publish(@PathVariable UUID id) {
        return productMapper.toDetail(adminProductService.publish(id), jsonMapper);
    }

    @PatchMapping("/{id}/unpublish")
    public ProductDetailDto unpublish(@PathVariable UUID id) {
        return productMapper.toDetail(adminProductService.unpublish(id), jsonMapper);
    }

    @PutMapping("/{id}")
    public ProductDetailDto update(@PathVariable UUID id, @Valid @RequestBody ProductUpdateRequest req) {
        return productMapper.toDetail(adminProductService.update(id, req), jsonMapper);
    }
}

