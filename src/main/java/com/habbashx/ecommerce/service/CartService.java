package com.habbashx.ecommerce.service;

import com.habbashx.ecommerce.dto.response.CartItemResponse;
import com.habbashx.ecommerce.dto.response.CartResponse;
import com.habbashx.ecommerce.entity.Cart;
import com.habbashx.ecommerce.entity.CartItem;
import com.habbashx.ecommerce.entity.Product;
import com.habbashx.ecommerce.entity.User;
import com.habbashx.ecommerce.repository.CartItemRepository;
import com.habbashx.ecommerce.repository.CartRepository;
import com.habbashx.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    public CartResponse getCart(User user) {

        final Cart cart = getOrCreateCart(user);

        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(@NotNull final User user , final Integer productId ,final Integer quantity) {

        Product product = productRepository.findProductById(productId).orElseThrow(
                () -> new RuntimeException("product not found")
        );

        if (!product.getIsActive()) {
            throw new RuntimeException("product is not available");
        }

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("not enough stock available: "+product.getStockQuantity());
        }

        final Cart cart = getOrCreateCart(user);

        @Nullable final CartItem cartItem = cartItemRepository.findByCartAndProduct(cart,product)
                .orElse(null);

        if (cartItem != null) {
            int newQuantity = cartItem.getQuantity() + quantity;

            if (newQuantity > product.getStockQuantity()) {
                throw new RuntimeException("not enough stock available: "+product.getStockQuantity());
            }

            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {

            final CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .quantity(quantity)
                    .product(product)
                    .build();

            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(@NotNull final User user , final Integer cartItemId , final Integer newQuantity) {

        final CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("cannot find cart item with this id"));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("un authorized modification");
        }

        if (newQuantity <= 0) {
            cartItem.getCart().getItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            if (newQuantity  > cartItem.getProduct().getStockQuantity()) {
                throw new RuntimeException("not enough stock available: "+cartItem.getProduct().getStockQuantity());
            }

            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        }
        Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }


    @Transactional
    public CartResponse removeItem(@NotNull final User user, final Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("cannot find cart item with this id"));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("un authorized modification");
        }

        cartItem.getCart().getItems().remove(cartItem);
        cartItemRepository.save(cartItem);

        final Cart cart = getOrCreateCart(user);
        return toResponse(cart);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(@NotNull final User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }



    private CartResponse toResponse(@NotNull final Cart cart) {

        List<CartItemResponse> itemResponses = cart.getItems()
                .stream().map(
                        cartItem -> {
                            BigDecimal subtotal = cartItem.getProduct().getPrice()
                                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                           return CartItemResponse.builder()
                                   .cartItemId(cartItem.getId())
                                   .productId(cartItem.getProduct().getId())
                                   .productName(cartItem.getProduct().getProductName())
                                   .productPrice(cartItem.getProduct().getPrice())
                                   .quantity(cartItem.getQuantity())
                                   .subtotal(subtotal)
                                   .build();
                        }
                ).toList();

        BigDecimal totalPrices = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);


        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .totalItems(itemResponses.size())
                .totalPrice(totalPrices)
                .build();
    }


}
