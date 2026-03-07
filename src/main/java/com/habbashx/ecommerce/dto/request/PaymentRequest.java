package com.habbashx.ecommerce.dto.request;

import com.habbashx.ecommerce.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentRequest {

    @NotNull(message = "order id is required")
    private Integer orderId;

    @NotNull(message = "payment method is required")
    private PaymentMethod paymentMethod;

}
