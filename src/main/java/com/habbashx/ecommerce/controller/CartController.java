package com.habbashx.ecommerce.controller;

import com.habbashx.ecommerce.dto.response.ApiResponse;
import com.habbashx.ecommerce.dto.response.CartResponse;
import com.habbashx.ecommerce.entity.User;
import com.habbashx.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal final User user) {

        return ResponseEntity.ok(
                ApiResponse.success("Cart fetched", cartService.getCart(user))
        );
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal final User user,
            @RequestParam final Integer productId,
            @RequestParam(defaultValue = "1") final Integer quantity) {

        return ResponseEntity.ok(
                ApiResponse.success("Item added to cart", cartService.addItem(user, productId, quantity))
        );
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @AuthenticationPrincipal final User user,
            @PathVariable final Integer cartItemId,
            @RequestParam final Integer quantity) {

        return ResponseEntity.ok(
                ApiResponse.success("Cart updated", cartService.updateItemQuantity(user, cartItemId, quantity))
        );
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal final User user,
            @PathVariable final Integer cartItemId) {

        return ResponseEntity.ok(
                ApiResponse.success("Item removed", cartService.removeItem(user, cartItemId))
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal final User user) {

        cartService.clearCart(user);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
}