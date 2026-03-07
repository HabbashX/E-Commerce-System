package com.habbashx.ecommerce.controller;

import com.habbashx.ecommerce.dto.request.PaymentRequest;
import com.habbashx.ecommerce.dto.response.ApiResponse;
import com.habbashx.ecommerce.dto.response.PaymentResponse;
import com.habbashx.ecommerce.entity.User;
import com.habbashx.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PaymentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment processed",
                        paymentService.pay(user, request)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Integer orderId) {

        return ResponseEntity.ok(
                ApiResponse.success("Payment fetched",
                        paymentService.getPaymentByOrder(user, orderId))
        );
    }

    @PutMapping("/order/{orderId}/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(
            @AuthenticationPrincipal User user,
            @PathVariable Integer orderId) {

        return ResponseEntity.ok(
                ApiResponse.success("Refund processed",
                        paymentService.refund(user, orderId))
        );
    }
}