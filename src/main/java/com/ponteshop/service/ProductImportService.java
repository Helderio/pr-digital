package com.ponteshop.service;

import com.ponteshop.dto.PriceBreakdownDto;
import com.ponteshop.dto.ProductPreviewDto;
import com.ponteshop.entity.Product;
import com.ponteshop.entity.Store;
import com.ponteshop.enums.ImportedBy;
import com.ponteshop.enums.ProductStatus;
import com.ponteshop.exception.ImportFailedException;
import com.ponteshop.exception.ResourceNotFoundException;
import com.ponteshop.repository.ProductRepository;
import com.ponteshop.repository.StoreRepository;
import com.ponteshop.util.FirecrawlClient;
import com.ponteshop.util.JsonMapper;
import com.ponteshop.util.ProductExtraction;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImportService {
    private final FirecrawlClient firecrawlClient;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final PriceCalculatorService priceCalculatorService;
    private final JsonMapper jsonMapper;

    @Transactional
    public ProductPreviewDto importFromUrl(String url, Integer storeId) {
        try {
            Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada"));
            if (!store.isActive()) {
                throw new ResourceNotFoundException("Loja não encontrada");
            }

            ProductExtraction extracted = firecrawlClient.scrapeProduct(url);

            if (extracted.name() == null || extracted.name().isBlank()) {
                throw new ImportFailedException("Extração sem nome do produto");
            }
            if (extracted.priceEur() == null || extracted.priceEur().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ImportFailedException("Extração sem preço EUR válido");
            }

            PriceCalculatorService.PriceCalculationResult calc = priceCalculatorService.calculatePriceAoa(extracted.priceEur());
            PriceBreakdownDto breakdown = calc.breakdown();

            List<String> images = extracted.images() == null ? List.of() : extracted.images();
            String imagesJson = jsonMapper.fromStringList(images);
            String variantsJson = jsonMapper.fromVariantList(
                extracted.variants() == null ? List.of() : extracted.variants()
            );

            Product product = Product.builder()
                .store(store)
                .sourceUrl(url)
                .name(extracted.name())
                .description(extracted.description())
                .category(extracted.category())
                .images(imagesJson)
                .variants(variantsJson)
                .priceEur(extracted.priceEur())
                .priceAoa(calc.totalAoa())
                .priceBreakdown(calc.breakdownJson())
                .importedBy(ImportedBy.AI)
                .status(ProductStatus.PENDING_REVIEW)
                .isFeatured(false)
                .build();

            Product saved = productRepository.save(product);

            return ProductPreviewDto.builder()
                .id(saved.getId())
                .storeId(store.getId())
                .sourceUrl(url)
                .name(product.getName())
                .priceEur(product.getPriceEur())
                .priceAoa(product.getPriceAoa())
                .description(product.getDescription())
                .category(product.getCategory())
                .images(images)
                .variants(extracted.variants() == null ? List.of() : extracted.variants())
                .priceBreakdown(breakdown)
                .build();
        } catch (ImportFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new ImportFailedException("Falha ao importar produto", e);
        }
    }
}
