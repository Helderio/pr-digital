package com.ponteshop.service;

import com.ponteshop.dto.CartDto;
import com.ponteshop.dto.CartItemDto;
import com.ponteshop.dto.CartItemRequest;
import com.ponteshop.dto.ProductSummaryDto;
import com.ponteshop.dto.mapper.ProductMapper;
import com.ponteshop.dto.mapper.SpecialRequestMapper;
import com.ponteshop.entity.CartItem;
import com.ponteshop.entity.Product;
import com.ponteshop.entity.SpecialRequest;
import com.ponteshop.entity.User;
import com.ponteshop.enums.CartItemType;
import com.ponteshop.enums.ProductStatus;
import com.ponteshop.enums.SpecialRequestStatus;
import com.ponteshop.exception.ResourceNotFoundException;
import com.ponteshop.repository.CartItemRepository;
import com.ponteshop.repository.ProductRepository;
import com.ponteshop.repository.SpecialRequestRepository;
import com.ponteshop.repository.UserRepository;
import com.ponteshop.util.JsonMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SpecialRequestRepository specialRequestRepository;
    private final ProductMapper productMapper;
    private final SpecialRequestMapper specialRequestMapper;
    private final JsonMapper jsonMapper;

    @Transactional(readOnly = true)
    public CartDto getCart(UUID userId) {
        List<CartItem> items = cartItemRepository.findByUserIdOrderByAddedAtDesc(userId);
        return toCartDto(items);
    }

    @Transactional
    public CartDto addItem(UUID userId, CartItemRequest req) {
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));

        if (req.getItemType() == CartItemType.CATALOG) {
            return addCatalogItem(userId, req);
        }
        return addSpecialRequestItem(userId, req);
    }

    private CartDto addCatalogItem(UUID userId, CartItemRequest req) {
        Product product = productRepository.findById(req.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ResourceNotFoundException("Produto não encontrado");
        }

        String variant = req.getSelectedVariant();
        CartItem item = cartItemRepository
            .findFirstByUserIdAndProductIdAndSelectedVariant(userId, product.getId(), variant)
            .orElse(null);

        if (item == null) {
            User user = userRepository.getReferenceById(userId);
            item = CartItem.builder()
                .user(user)
                .product(product)
                .specialRequest(null)
                .itemType(CartItemType.CATALOG)
                .quantity(req.getQuantity())
                .selectedVariant(variant)
                .priceAoaSnapshot(product.getPriceAoa() == null ? BigDecimal.ZERO : product.getPriceAoa())
                .build();
        } else {
            item.setQuantity(item.getQuantity() + req.getQuantity());
        }

        cartItemRepository.save(item);
        return getCart(userId);
    }

    private CartDto addSpecialRequestItem(UUID userId, CartItemRequest req) {
        SpecialRequest sr = specialRequestRepository.findByIdAndUser_Id(req.getSpecialRequestId(), userId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido especial não encontrado ou inválido"));
        if (sr.getStatus() != SpecialRequestStatus.PRICED) {
            throw new ResourceNotFoundException("Pedido especial não encontrado ou inválido");
        }

        String variant = req.getSelectedVariant();
        CartItem item = cartItemRepository
            .findFirstByUserIdAndSpecialRequestIdAndSelectedVariant(userId, sr.getId(), variant)
            .orElse(null);

        if (item == null) {
            User user = userRepository.getReferenceById(userId);
            item = CartItem.builder()
                .user(user)
                .product(null)
                .specialRequest(sr)
                .itemType(CartItemType.SPECIAL_REQUEST)
                .quantity(req.getQuantity())
                .selectedVariant(variant)
                .priceAoaSnapshot(sr.getCalculatedPriceAoa() == null ? BigDecimal.ZERO : sr.getCalculatedPriceAoa())
                .build();
            item = cartItemRepository.save(item);

            sr.setStatus(SpecialRequestStatus.IN_CART);
            sr.setCartItemId(item.getId());
            specialRequestRepository.save(sr);
        } else {
            item.setQuantity(item.getQuantity() + req.getQuantity());
            cartItemRepository.save(item);
        }

        return getCart(userId);
    }

    @Transactional
    public CartDto updateQuantity(UUID userId, UUID cartItemId, int quantity) {
        CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho não encontrado"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return getCart(userId);
    }

    @Transactional
    public CartDto deleteItem(UUID userId, UUID cartItemId) {
        CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho não encontrado"));

        if (item.getItemType() == CartItemType.SPECIAL_REQUEST && item.getSpecialRequest() != null) {
            SpecialRequest sr = item.getSpecialRequest();
            sr.setStatus(SpecialRequestStatus.PRICED);
            sr.setCartItemId(null);
            specialRequestRepository.save(sr);
        }

        cartItemRepository.delete(item);
        return getCart(userId);
    }

    @Transactional
    public void clear(UUID userId) {
        List<CartItem> items = cartItemRepository.findByUserIdOrderByAddedAtDesc(userId);
        for (CartItem item : items) {
            if (item.getItemType() == CartItemType.SPECIAL_REQUEST && item.getSpecialRequest() != null) {
                SpecialRequest sr = item.getSpecialRequest();
                sr.setStatus(SpecialRequestStatus.PRICED);
                sr.setCartItemId(null);
                specialRequestRepository.save(sr);
            }
        }
        cartItemRepository.deleteByUserId(userId);
    }

    private CartDto toCartDto(List<CartItem> items) {
        List<CartItemDto> dtoItems = items.stream().map(this::toCartItemDto).toList();
        BigDecimal total = dtoItems.stream()
            .map(i -> i.getPriceAoa().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDto.builder()
            .items(dtoItems)
            .totalAoa(total)
            .itemCount(dtoItems.stream().mapToInt(i -> i.getQuantity()).sum())
            .build();
    }

    private CartItemDto toCartItemDto(CartItem item) {
        CartItemDto.CartItemDtoBuilder b = CartItemDto.builder()
            .id(item.getId())
            .itemType(item.getItemType() != null ? item.getItemType() : CartItemType.CATALOG)
            .quantity(item.getQuantity())
            .selectedVariant(item.getSelectedVariant())
            .priceAoa(item.getPriceAoaSnapshot());

        if (item.getItemType() == CartItemType.SPECIAL_REQUEST) {
            b.product(null);
            if (item.getSpecialRequest() != null) {
                b.specialRequest(specialRequestMapper.toDto(item.getSpecialRequest(), jsonMapper));
            }
        } else {
            b.specialRequest(null);
            if (item.getProduct() != null) {
                ProductSummaryDto product = productMapper.toSummary(item.getProduct(), jsonMapper);
                b.product(product);
            }
        }

        return b.build();
    }
}
