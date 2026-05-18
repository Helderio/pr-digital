package com.ponteshop.controller;

import com.ponteshop.dto.CartDto;
import com.ponteshop.dto.CartItemRequest;
import com.ponteshop.dto.UpdateQuantityRequest;
import com.ponteshop.security.SecurityUtil;
import com.ponteshop.service.CartService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public CartDto get() {
        return cartService.getCart(securityUtil.requireCurrentUserId());
    }

    @PostMapping("/items")
    public CartDto add(@Valid @RequestBody CartItemRequest req) {
        return cartService.addItem(securityUtil.requireCurrentUserId(), req);
    }

    @PutMapping("/items/{id}")
    public CartDto updateQty(@PathVariable UUID id, @Valid @RequestBody UpdateQuantityRequest req) {
        return cartService.updateQuantity(securityUtil.requireCurrentUserId(), id, req.getQuantity());
    }

    @DeleteMapping("/items/{id}")
    public CartDto deleteItem(@PathVariable UUID id) {
        return cartService.deleteItem(securityUtil.requireCurrentUserId(), id);
    }

    @DeleteMapping
    public ResponseEntity<Void> clear() {
        cartService.clear(securityUtil.requireCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}

