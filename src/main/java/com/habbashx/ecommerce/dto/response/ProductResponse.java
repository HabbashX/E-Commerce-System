package com.habbashx.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Integer id;
    private String productName;
    private String productDesc;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean isActive;
    private Set<CategoryResponse> categories;
}
