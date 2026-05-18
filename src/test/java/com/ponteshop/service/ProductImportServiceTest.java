package com.ponteshop.service;

import com.ponteshop.dto.PriceBreakdownDto;
import com.ponteshop.dto.VariantOptionDto;
import com.ponteshop.entity.Product;
import com.ponteshop.entity.Store;
import com.ponteshop.enums.ImportedBy;
import com.ponteshop.enums.ProductStatus;
import com.ponteshop.exception.ImportFailedException;
import com.ponteshop.repository.ProductRepository;
import com.ponteshop.repository.StoreRepository;
import com.ponteshop.util.FirecrawlClient;
import com.ponteshop.util.JsonMapper;
import com.ponteshop.util.ProductExtraction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductImportServiceTest {
    @Test
    void importFromUrl_savesPendingReviewProduct() {
        FirecrawlClient firecrawl = Mockito.mock(FirecrawlClient.class);
        StoreRepository storeRepo = Mockito.mock(StoreRepository.class);
        ProductRepository productRepo = Mockito.mock(ProductRepository.class);
        PriceCalculatorService priceCalculatorService = Mockito.mock(PriceCalculatorService.class);
        JsonMapper jsonMapper = Mockito.mock(JsonMapper.class);

        ProductImportService service = new ProductImportService(
            firecrawl,
            storeRepo,
            productRepo,
            priceCalculatorService,
            jsonMapper
        );

        Store store = Store.builder().id(1).name("Zara").slug("zara").baseUrl("https://zara.com/pt").isActive(true).build();
        Mockito.when(storeRepo.findById(1)).thenReturn(Optional.of(store));
        Mockito.when(firecrawl.scrapeProduct("https://example.com/p")).thenReturn(new ProductExtraction(
            "Camisola",
            new BigDecimal("69.95"),
            "Descrição",
            "moda",
            "Zara",
            List.of("https://img/1.jpg"),
            List.of()
        ));
        PriceBreakdownDto breakdown = PriceBreakdownDto.builder()
            .productAoa(new BigDecimal("10.00"))
            .exchangeFee(new BigDecimal("1.00"))
            .serviceFeeAoa(new BigDecimal("2.00"))
            .shippingAoa(new BigDecimal("3.00"))
            .total(new BigDecimal("16.00"))
            .build();
        Mockito.when(priceCalculatorService.calculatePriceAoa(new BigDecimal("69.95")))
            .thenReturn(new PriceCalculatorService.PriceCalculationResult(new BigDecimal("16.00"), breakdown, "{\"total\":16}"));
        Mockito.when(jsonMapper.fromStringList(List.of("https://img/1.jpg"))).thenReturn("[\"https://img/1.jpg\"]");
        Mockito.when(jsonMapper.fromVariantList(List.of())).thenReturn("[]");
        Mockito.when(productRepo.save(Mockito.any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.importFromUrl("https://example.com/p", 1);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepo).save(captor.capture());
        Product saved = captor.getValue();

        assertThat(saved.getStore().getId()).isEqualTo(1);
        assertThat(saved.getSourceUrl()).isEqualTo("https://example.com/p");
        assertThat(saved.getName()).isEqualTo("Camisola");
        assertThat(saved.getImportedBy()).isEqualTo(ImportedBy.AI);
        assertThat(saved.getStatus()).isEqualTo(ProductStatus.PENDING_REVIEW);
        assertThat(saved.getPriceEur()).isEqualByComparingTo("69.95");
        assertThat(saved.getPriceAoa()).isEqualByComparingTo("16.00");
    }

    @Test
    void importFromUrl_throwsWhenFirecrawlMissingName() {
        FirecrawlClient firecrawl = Mockito.mock(FirecrawlClient.class);
        StoreRepository storeRepo = Mockito.mock(StoreRepository.class);
        ProductRepository productRepo = Mockito.mock(ProductRepository.class);
        PriceCalculatorService priceCalculatorService = Mockito.mock(PriceCalculatorService.class);
        JsonMapper jsonMapper = Mockito.mock(JsonMapper.class);

        ProductImportService service = new ProductImportService(
            firecrawl,
            storeRepo,
            productRepo,
            priceCalculatorService,
            jsonMapper
        );

        Store store = Store.builder().id(1).name("Zara").slug("zara").baseUrl("https://zara.com/pt").isActive(true).build();
        Mockito.when(storeRepo.findById(1)).thenReturn(Optional.of(store));
        Mockito.when(firecrawl.scrapeProduct(Mockito.anyString())).thenReturn(new ProductExtraction(
            "",
            new BigDecimal("10.00"),
            "Desc",
            "moda",
            "Zara",
            List.of(),
            List.of()
        ));

        assertThatThrownBy(() -> service.importFromUrl("https://example.com/p", 1))
            .isInstanceOf(ImportFailedException.class)
            .hasMessageContaining("nome");
    }

    @Test
    void importFromUrl_propagatesFirecrawlFailure() {
        FirecrawlClient firecrawl = Mockito.mock(FirecrawlClient.class);
        StoreRepository storeRepo = Mockito.mock(StoreRepository.class);
        ProductRepository productRepo = Mockito.mock(ProductRepository.class);
        PriceCalculatorService priceCalculatorService = Mockito.mock(PriceCalculatorService.class);
        JsonMapper jsonMapper = Mockito.mock(JsonMapper.class);

        ProductImportService service = new ProductImportService(
            firecrawl,
            storeRepo,
            productRepo,
            priceCalculatorService,
            jsonMapper
        );

        Store store = Store.builder().id(1).name("Zara").slug("zara").baseUrl("https://zara.com/pt").isActive(true).build();
        Mockito.when(storeRepo.findById(1)).thenReturn(Optional.of(store));
        Mockito.when(firecrawl.scrapeProduct(Mockito.anyString())).thenThrow(new ImportFailedException("falhou"));

        assertThatThrownBy(() -> service.importFromUrl("https://example.com/p", 1))
            .isInstanceOf(ImportFailedException.class);
    }
}
