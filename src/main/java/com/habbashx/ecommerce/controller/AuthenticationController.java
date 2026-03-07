package com.habbashx.ecommerce.controller;

import com.habbashx.ecommerce.dto.response.ApiResponse;
import com.habbashx.ecommerce.dto.response.AuthenticationResponse;
import com.habbashx.ecommerce.dto.request.LoginRequest;
import com.habbashx.ecommerce.dto.request.RegisterRequest;
import com.habbashx.ecommerce.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @Valid @RequestBody final RegisterRequest registerRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "registered successfully",
                        authenticationService.register(registerRequest)
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(
            @Valid @RequestBody final LoginRequest loginRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "login successfully",
                        authenticationService.login(loginRequest)
                )
        );
    }

}
