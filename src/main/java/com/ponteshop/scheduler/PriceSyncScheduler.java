package com.ponteshop.scheduler;

import com.ponteshop.enums.ProductStatus;
import com.ponteshop.repository.ProductRepository;
import com.ponteshop.repository.ProductSpecifications;
import com.ponteshop.service.PriceCalculatorService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceSyncScheduler {
    private final ProductRepository productRepository;
    private final PriceCalculatorService priceCalculatorService;

    @Scheduled(cron = "0 0 3 * * MON")
    @Transactional
    public void syncProductPrices() {
        Specification<com.ponteshop.entity.Product> spec = Specification.where(ProductSpecifications.status(ProductStatus.ACTIVE));
        Pageable pageable = PageRequest.of(0, 200);
        Page<com.ponteshop.entity.Product> page;

        do {
            page = productRepository.findAll(spec, pageable);
            page.getContent().forEach(p -> {
                if (p.getPriceEur() == null) return;
                var calc = priceCalculatorService.calculatePriceAoa(p.getPriceEur());
                p.setPriceAoa(calc.totalAoa());
                p.setPriceBreakdown(calc.breakdownJson());
                p.setLastSyncedAt(LocalDateTime.now());
            });
            pageable = page.nextPageable();
        } while (page.hasNext());
    }
}

