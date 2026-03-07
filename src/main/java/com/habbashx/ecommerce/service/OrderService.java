package com.habbashx.ecommerce.service;

import com.habbashx.ecommerce.dto.response.AddressResponse;
import com.habbashx.ecommerce.dto.request.CheckoutRequest;
import com.habbashx.ecommerce.dto.response.OrderItemResponse;
import com.habbashx.ecommerce.dto.response.OrderResponse;
import com.habbashx.ecommerce.entity.*;
import com.habbashx.ecommerce.exception.CartException;
import com.habbashx.ecommerce.repository.AddressRepository;
import com.habbashx.ecommerce.repository.CartRepository;
import com.habbashx.ecommerce.repository.OrderRepository;
import com.habbashx.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final CartRepository cartRepository;

    private final AddressRepository addressRepository;

    private final ProductRepository productRepository;

    private final CartService cartService;

    @Transactional
    public OrderResponse checkout(@NotNull final User user, @NotNull final CheckoutRequest checkoutRequest) {
        final Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartException("cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new CartException("Cannot checkout with an empty cart");
        }

        final Address address = addressRepository.findByIdAndUser(checkoutRequest.getShippingAddressId(),user)
                .orElseThrow(() -> new RuntimeException("Address not found"));


        final List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
                    Product product =cartItem.getProduct();

                    if (product.getStockQuantity() < cartItem.getQuantity()) {
                        throw new RuntimeException("not enough stock for product: "+product.getProductName());
                    }

                    product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
                    productRepository.save(product);

                    return OrderItem.builder()
                            .product(product)
                            .quantity(cartItem.getQuantity())
                            .priceAtPurchase(product.getPrice())
                            .build();
                }
        ).toList();

        final BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getPriceAtPurchase()
                        .multiply(valueOf(item.getQuantity())))
                .reduce(ZERO,BigDecimal::add);

        final Order order = Order.builder()
                .user(user)
                .shippingAddress(address)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .orderItems(orderItems)
                .build();

        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        orderRepository.save(order);

        cartService.clearCart(user);

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(@NotNull final User user, Pageable pageable) {
        return orderRepository.findOrderByUser(user,pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(final Integer id, final User user) {
        final Order order = orderRepository.findByIdAndUser(id,user).orElseThrow(
                () -> new RuntimeException("order is not found with this id: " + id)
        );
        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(final User user , final Integer orderId) {

        final Order order = orderRepository.findByIdAndUser(orderId,user).orElseThrow(
                () -> new RuntimeException("order not found with this id: "+orderId)
        );

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("cannot cancel order, current status is: "+order.getOrderStatus());
        }

        order.getOrderItems().forEach( orderItem -> {
                Product product = orderItem.getProduct();
                product.setStockQuantity(product.getStockQuantity()+orderItem.getQuantity());
                productRepository.save(product);
        });
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(final Integer orderId , final OrderStatus status) {

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("order not found with this id")
        );
        order.setOrderStatus(status);

        orderRepository.save(order);

        return toResponse(order);
    }

    private OrderResponse toResponse(@NotNull final Order order) {
        final List<OrderItemResponse> orderItemResponses = order.getOrderItems()
                .stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getProductName())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPriceAtPurchase())
                        .subtotal(item.getPriceAtPurchase().multiply(valueOf(item.getQuantity())))
                        .build())
                .toList();

        final Address address = order.getShippingAddress();

        final AddressResponse addressResponse = AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .street(address.getStreet())
                .city(address.getCity())
                .stateName(address.getStateName())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();

        return OrderResponse.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus())
                .orderItemResponses(orderItemResponses)
                .addressResponse(addressResponse)
                .createAt(order.getCreatedAt())
                .totalAmount(order.getTotalAmount())
                .build();

    }
}
