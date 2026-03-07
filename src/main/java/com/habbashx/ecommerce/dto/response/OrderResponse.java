package com.habbashx.ecommerce.dto.response;

import com.habbashx.ecommerce.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class OrderResponse {

    private Integer id;

    private OrderStatus orderStatus;

    private BigDecimal totalAmount;

    private LocalDateTime createAt;

    private AddressResponse addressResponse;

    private List<OrderItemResponse> orderItemResponses = new ArrayList<>();
}
