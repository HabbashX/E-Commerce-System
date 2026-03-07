package com.habbashx.ecommerce.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "username is required")
    @Size(min = 3 ,max = 30 ,message ="username must be between 3 - 30 ")
    private String username;

    @NotBlank(message = "email is required")
    @Email(message ="email must be valid")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6 , message  ="password may not be less than 6 character")
    private String password;
}
