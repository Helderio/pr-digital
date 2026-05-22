package com.ponteshop.service;

import com.ponteshop.dto.ProductDetailDto;
import com.ponteshop.dto.mapper.ProductMapper;
import com.ponteshop.dto.mapper.SpecialRequestMapper;
import com.ponteshop.dto.specialrequest.SpecialRequestAdminDto;
import com.ponteshop.dto.specialrequest.SpecialRequestDto;
import com.ponteshop.entity.Product;
import com.ponteshop.entity.SpecialRequest;
import com.ponteshop.entity.Store;
import com.ponteshop.enums.ImportedBy;
import com.ponteshop.enums.ProductStatus;
import com.ponteshop.enums.SpecialRequestStatus;
import com.ponteshop.exception.ImportFailedException;
import com.ponteshop.exception.ResourceNotFoundException;
import com.ponteshop.exception.SpecialRequestStateException;
import com.ponteshop.repository.ProductRepository;
import com.ponteshop.repository.SpecialRequestRepository;
import com.ponteshop.repository.SpecialRequestSpecifications;
import com.ponteshop.repository.StoreRepository;
import com.ponteshop.repository.UserRepository;
import com.ponteshop.util.FirecrawlClient;
import com.ponteshop.util.JsonMapper;
import com.ponteshop.util.ProductExtraction;
import com.ponteshop.util.StoreUrlValidator;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpecialRequestService {

    private final SpecialRequestRepository specialRequestRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final FirecrawlClient firecrawlClient;
    private final PriceCalculatorService priceCalculatorService;
    private final JsonMapper jsonMapper;
    private final SpecialRequestMapper specialRequestMapper;
    private final ProductMapper productMapper;

    @Transactional
    public SpecialRequestDto analyseUrl(String url, Integer storeId, UUID userId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada"));
        if (!store.isActive()) {
            throw new ResourceNotFoundException("Loja não encontrada");
        }
        StoreUrlValidator.requireBelongsToStore(url, store);

        SpecialRequest request = SpecialRequest.builder()
            .user(userRepository.getReferenceById(userId))
            .store(store)
            .sourceUrl(url)
            .status(SpecialRequestStatus.ANALYSING)
            .build();
        request = specialRequestRepository.save(request);

        try {
            ProductExtraction extracted = firecrawlClient.scrapeProduct(url);
            PriceCalculatorService.PriceCalculationResult calc = priceCalculatorService.calculatePriceAoa(extracted.priceEur());

            request.setDetectedName(extracted.name());
            request.setDetectedDescription(extracted.description());
            request.setDetectedImages(jsonMapper.fromStringList(extracted.images() == null ? List.of() : extracted.images()));
            request.setDetectedVariants(jsonMapper.fromVariantList(
                extracted.variants() == null ? List.of() : extracted.variants()
            ));
            request.setDetectedPriceEur(extracted.priceEur());
            request.setCalculatedPriceAoa(calc.totalAoa());
            request.setPriceBreakdown(calc.breakdownJson());
            request.setStatus(SpecialRequestStatus.PRICED);
        } catch (ImportFailedException e) {
            request.setStatus(SpecialRequestStatus.AI_FAILED);
            specialRequestRepository.save(request);
            throw new ImportFailedException("Não foi possível analisar este produto. Tenta com outro link.", e);
        } catch (Exception e) {
            request.setStatus(SpecialRequestStatus.AI_FAILED);
            specialRequestRepository.save(request);
            throw new ImportFailedException("Não foi possível analisar este produto. Tenta com outro link.", e);
        }

        SpecialRequest saved = specialRequestRepository.save(request);
        return specialRequestMapper.toDto(saved, jsonMapper);
    }

    @Transactional(readOnly = true)
    public List<SpecialRequestDto> listMine(UUID userId) {
        return specialRequestRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
            .map(sr -> specialRequestMapper.toDto(sr, jsonMapper))
            .toList();
    }

    @Transactional(readOnly = true)
    public SpecialRequestDto getMine(UUID userId, UUID id) {
        SpecialRequest sr = specialRequestRepository.findByIdAndUser_Id(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido especial não encontrado"));
        return specialRequestMapper.toDto(sr, jsonMapper);
    }

    @Transactional(readOnly = true)
    public Page<SpecialRequestAdminDto> listAdmin(SpecialRequestStatus status, Integer storeId, Pageable pageable) {
        Specification<SpecialRequest> spec = SpecialRequestSpecifications.adminFilters(status, storeId);
        return specialRequestRepository.findAll(spec, pageable)
            .map(sr -> specialRequestMapper.toAdminDto(sr, jsonMapper));
    }

    @Transactional(readOnly = true)
    public SpecialRequestAdminDto getAdmin(UUID id) {
        SpecialRequest sr = specialRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido especial não encontrado"));
        return specialRequestMapper.toAdminDto(sr, jsonMapper);
    }

    @Transactional
    public SpecialRequestAdminDto updateStatusAdmin(UUID id, SpecialRequestStatus status) {
        SpecialRequest sr = specialRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido especial não encontrado"));
        sr.setStatus(status);
        SpecialRequest saved = specialRequestRepository.save(sr);
        return specialRequestMapper.toAdminDto(saved, jsonMapper);
    }

    @Transactional
    public ProductDetailDto publishToCatalog(UUID specialRequestId) {
        SpecialRequest sr = specialRequestRepository.findById(specialRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido especial não encontrado"));
        if (sr.getProductId() != null) {
            throw new SpecialRequestStateException("Este pedido já foi publicado no catálogo");
        }

        String name = sr.getDetectedName() != null && !sr.getDetectedName().isBlank()
            ? sr.getDetectedName()
            : "Produto";

        Product product = Product.builder()
            .store(sr.getStore())
            .sourceUrl(sr.getSourceUrl())
            .name(name)
            .description(sr.getDetectedDescription())
            .category("outro")
            .images(sr.getDetectedImages() != null ? sr.getDetectedImages() : "[]")
            .variants(sr.getDetectedVariants() != null ? sr.getDetectedVariants() : "[]")
            .priceEur(sr.getDetectedPriceEur())
            .priceAoa(sr.getCalculatedPriceAoa() != null ? sr.getCalculatedPriceAoa() : BigDecimal.ZERO)
            .priceBreakdown(sr.getPriceBreakdown())
            .importedBy(ImportedBy.AI)
            .status(ProductStatus.ACTIVE)
            .isFeatured(false)
            .build();

        Product saved = productRepository.save(product);
        sr.setProductId(saved.getId());
        specialRequestRepository.save(sr);

        return productMapper.toDetail(saved, jsonMapper);
    }
}
