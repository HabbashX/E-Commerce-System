package com.habbashx.ecommerce.controller;

import com.habbashx.ecommerce.dto.response.ApiResponse;
import com.habbashx.ecommerce.dto.request.CheckoutRequest;
import com.habbashx.ecommerce.dto.response.OrderResponse;
import com.habbashx.ecommerce.entity.OrderStatus;
import com.habbashx.ecommerce.entity.User;
import com.habbashx.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CheckoutRequest checkoutRequest
            ) {
        return ResponseEntity.ok(
                ApiResponse.success("Order placed successfully",orderService.checkout(user,checkoutRequest))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                ApiResponse.success("Orders fetched",
                        orderService.getMyOrders(user,
                                PageRequest.of(page, size, Sort.by("createdAt").descending())))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @AuthenticationPrincipal User user,
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success("Order fetched", orderService.getOrderById(id,user))
        );
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success("Order cancelled", orderService.cancelOrder(user, id))
        );
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Integer id,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success("Order status updated",
                        orderService.updateOrderStatus(id, status))
        );
    }

}
