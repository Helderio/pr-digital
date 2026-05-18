package com.ponteshop.service;

import com.ponteshop.dto.CartItemRequest;
import com.ponteshop.dto.ProductSummaryDto;
import com.ponteshop.dto.mapper.ProductMapper;
import com.ponteshop.dto.mapper.SpecialRequestMapper;
import com.ponteshop.dto.specialrequest.SpecialRequestDto;
import com.ponteshop.entity.CartItem;
import com.ponteshop.entity.Product;
import com.ponteshop.entity.SpecialRequest;
import com.ponteshop.entity.Store;
import com.ponteshop.entity.User;
import com.ponteshop.enums.CartItemType;
import com.ponteshop.enums.ProductStatus;
import com.ponteshop.enums.SpecialRequestStatus;
import com.ponteshop.repository.CartItemRepository;
import com.ponteshop.repository.ProductRepository;
import com.ponteshop.repository.SpecialRequestRepository;
import com.ponteshop.repository.UserRepository;
import com.ponteshop.util.JsonMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class CartServiceTest {

    @Test
    void addItem_catalog_mergesSameVariant() {
        CartItemRepository cartRepo = Mockito.mock(CartItemRepository.class);
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        ProductRepository productRepo = Mockito.mock(ProductRepository.class);
        SpecialRequestRepository srRepo = Mockito.mock(SpecialRequestRepository.class);
        ProductMapper productMapper = Mockito.mock(ProductMapper.class);
        SpecialRequestMapper srMapper = Mockito.mock(SpecialRequestMapper.class);
        JsonMapper jsonMapper = Mockito.mock(JsonMapper.class);

        CartService service = new CartService(cartRepo, userRepo, productRepo, srRepo, productMapper, srMapper, jsonMapper);

        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Store store = Store.builder().id(1).slug("zara").build();
        Product product = Product.builder()
            .id(productId)
            .store(store)
            .name("Camisa")
            .status(ProductStatus.ACTIVE)
            .priceAoa(new BigDecimal("100.00"))
            .build();

        Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        Mockito.when(productRepo.findById(productId)).thenReturn(Optional.of(product));
        Mockito.when(cartRepo.findFirstByUserIdAndProductIdAndSelectedVariant(userId, productId, null))
            .thenReturn(Optional.empty());
        Mockito.when(userRepo.getReferenceById(userId)).thenReturn(User.builder().id(userId).build());
        CartItem saved = CartItem.builder()
            .id(UUID.randomUUID())
            .user(User.builder().id(userId).build())
            .product(product)
            .itemType(CartItemType.CATALOG)
            .quantity(2)
            .priceAoaSnapshot(new BigDecimal("100.00"))
            .build();
        Mockito.when(cartRepo.save(any())).thenReturn(saved);
        Mockito.when(cartRepo.findByUserIdOrderByAddedAtDesc(userId)).thenReturn(List.of(saved));
        Mockito.when(productMapper.toSummary(product, jsonMapper)).thenReturn(ProductSummaryDto.builder().id(productId).name("Camisa").build());

        var cart = service.addItem(userId, CartItemRequest.builder()
            .itemType(CartItemType.CATALOG)
            .productId(productId)
            .quantity(2)
            .selectedVariant(null)
            .build());

        assertThat(cart.getItemCount()).isEqualTo(2);
        assertThat(cart.getTotalAoa()).isEqualByComparingTo("200.00");
    }

    @Test
    void addItem_specialRequest_setsInCart() {
        CartItemRepository cartRepo = Mockito.mock(CartItemRepository.class);
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        ProductRepository productRepo = Mockito.mock(ProductRepository.class);
        SpecialRequestRepository srRepo = Mockito.mock(SpecialRequestRepository.class);
        ProductMapper productMapper = Mockito.mock(ProductMapper.class);
        SpecialRequestMapper srMapper = Mockito.mock(SpecialRequestMapper.class);
        JsonMapper jsonMapper = Mockito.mock(JsonMapper.class);

        CartService service = new CartService(cartRepo, userRepo, productRepo, srRepo, productMapper, srMapper, jsonMapper);

        UUID userId = UUID.randomUUID();
        UUID srId = UUID.randomUUID();
        Store store = Store.builder().id(1).slug("zara").build();
        SpecialRequest sr = SpecialRequest.builder()
            .id(srId)
            .store(store)
            .status(SpecialRequestStatus.PRICED)
            .calculatedPriceAoa(new BigDecimal("500.00"))
            .build();

        Mockito.when(userRepo.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        Mockito.when(srRepo.findByIdAndUser_Id(srId, userId)).thenReturn(Optional.of(sr));
        Mockito.when(cartRepo.findFirstByUserIdAndSpecialRequestIdAndSelectedVariant(userId, srId, "{}"))
            .thenReturn(Optional.empty());
        Mockito.when(userRepo.getReferenceById(userId)).thenReturn(User.builder().id(userId).build());

        UUID cartItemId = UUID.randomUUID();
        Mockito.when(cartRepo.save(any())).thenAnswer(inv -> {
            CartItem ci = inv.getArgument(0);
            ci.setId(cartItemId);
            return ci;
        });

        CartItem line = CartItem.builder()
            .id(cartItemId)
            .user(User.builder().id(userId).build())
            .specialRequest(sr)
            .itemType(CartItemType.SPECIAL_REQUEST)
            .quantity(1)
            .selectedVariant("{}")
            .priceAoaSnapshot(new BigDecimal("500.00"))
            .build();
        Mockito.when(cartRepo.findByUserIdOrderByAddedAtDesc(userId)).thenReturn(List.of(line));
        Mockito.when(srMapper.toDto(sr, jsonMapper)).thenReturn(SpecialRequestDto.builder().id(srId).calculatedPriceAoa(new BigDecimal("500")).build());

        service.addItem(userId, CartItemRequest.builder()
            .itemType(CartItemType.SPECIAL_REQUEST)
            .specialRequestId(srId)
            .quantity(1)
            .selectedVariant("{}")
            .build());

        ArgumentCaptor<SpecialRequest> srCap = ArgumentCaptor.forClass(SpecialRequest.class);
        Mockito.verify(srRepo).save(srCap.capture());
        assertThat(srCap.getValue().getStatus()).isEqualTo(SpecialRequestStatus.IN_CART);
        assertThat(srCap.getValue().getCartItemId()).isEqualTo(cartItemId);
    }
}
