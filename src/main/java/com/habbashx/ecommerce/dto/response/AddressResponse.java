package com.habbashx.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {

    private Integer id;
    private String fullName;
    private String street;
    private String city;
    private String stateName;
    private String postalCode;
    private String country;
}
