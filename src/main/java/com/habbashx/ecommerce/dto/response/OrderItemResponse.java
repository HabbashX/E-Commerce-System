package com.habbashx.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {

    private Integer id;

    private Integer productId;

    private String productName;

    private Integer quantity;

    private BigDecimal priceAtPurchase;

    private BigDecimal subtotal;
}
