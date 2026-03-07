package com.habbashx.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "username or email is required")
    private String usernameOrEmail;

    @NotBlank(message = "password is required")
    private String password;
}
