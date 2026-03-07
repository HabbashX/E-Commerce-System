package com.habbashx.ecommerce.service;

import com.habbashx.ecommerce.dto.request.PaymentRequest;
import com.habbashx.ecommerce.dto.response.PaymentResponse;
import com.habbashx.ecommerce.entity.Order;
import com.habbashx.ecommerce.entity.OrderStatus;
import com.habbashx.ecommerce.entity.Payment;
import com.habbashx.ecommerce.entity.User;
import com.habbashx.ecommerce.payment.PaymentMethod;
import com.habbashx.ecommerce.payment.PaymentStatus;
import com.habbashx.ecommerce.repository.OrderRepository;
import com.habbashx.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;


    @Transactional
    public PaymentResponse pay(final User user , final @NotNull PaymentRequest paymentRequest) {

        Order order = orderRepository.findByIdAndUser(paymentRequest.getOrderId(), user)
                .orElseThrow(() -> new RuntimeException("order not found with this id: "+paymentRequest.getOrderId()));


        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new RuntimeException(
                    "Cannot pay for order — current status is: " + order.getOrderStatus()
            );
        }

        if (paymentRepository.existsByOrder(order)) {
            throw new RuntimeException("order is already paid");
        }

        final PaymentStatus paymentStatus = simulatePayment(paymentRequest.getPaymentMethod());
        final String transactionId = UUID.randomUUID().toString().toUpperCase();

        final boolean isPaymentCompleted = paymentStatus == PaymentStatus.COMPLETED;

        final Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(paymentRequest.getPaymentMethod())
                .paymentStatus(paymentStatus)
                .transactionId(transactionId)
                .paidAt(isPaymentCompleted ? LocalDateTime.now() : null)
                .build();

        paymentRepository.save(payment);

        if (paymentStatus == PaymentStatus.COMPLETED) {
            order.setOrderStatus(OrderStatus.SHIPPED);
            orderRepository.save(order);
            log.info("Payment successful for order {} — transaction: {}", order.getId(), transactionId);
        } else {
            log.warn("Payment failed for order {} — method: {}", order.getId(), paymentRequest.getPaymentMethod());
        }

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrder(final User user , final Integer orderId) {
        final Order order = orderRepository.findByIdAndUser(orderId,user)
                .orElseThrow(() -> new RuntimeException("order not found with this id: "+orderId));

        final Payment payment = paymentRepository.findByOrder(order).orElseThrow(
                () -> new RuntimeException("payment not found")
        );

        return toResponse(payment);
    }


    @Transactional
    public PaymentResponse refund(final User user , final Integer orderId) {
        final Order order = orderRepository.findByIdAndUser(orderId,user)
                .orElseThrow(() -> new RuntimeException("order not found with this id: "+orderId));

        final Payment payment = paymentRepository.findByOrder(order).orElseThrow(
                () -> new RuntimeException("payment not found")
        );

        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException(
                    "Cannot refund — payment status is: " + payment.getPaymentStatus()
            );
        }

        order.getOrderItems().forEach(
                items ->  {
                    items.getProduct().setStockQuantity(
                            items.getProduct().getStockQuantity() + items.getQuantity()
                    );
                }
        );
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        log.info("Refund processed for order {}", order.getId());

        return toResponse(payment);

    }

    private PaymentStatus simulatePayment(PaymentMethod method) {
        if (method == PaymentMethod.CASH_ON_DELIVERY) {
            return PaymentStatus.COMPLETED;
        }
        return Math.random() < 0.9
                ? PaymentStatus.COMPLETED
                : PaymentStatus.FAILED;
    }

    private @NotNull PaymentResponse toResponse(@NotNull final Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())

                .amount(payment.getOrder().getTotalAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .paidAt(payment.getPaidAt())
                .build();
    }
}

