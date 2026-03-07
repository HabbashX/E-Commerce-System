package com.habbashx.ecommerce.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CheckoutRequest {

    private Integer shippingAddressId;
}
