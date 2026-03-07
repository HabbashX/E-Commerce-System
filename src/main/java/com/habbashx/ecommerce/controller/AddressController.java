package com.habbashx.ecommerce.controller;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;


import com.habbashx.ecommerce.dto.request.AddressRequest;
import com.habbashx.ecommerce.dto.response.AddressResponse;
import com.habbashx.ecommerce.dto.response.ApiResponse;
import com.habbashx.ecommerce.entity.Address;
import com.habbashx.ecommerce.entity.User;
import com.habbashx.ecommerce.repository.AddressRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") final Integer page,
            @RequestParam(defaultValue = "0") final Integer size
    ) {

        List<AddressResponse> addresses = addressRepository.findByUser(user, PageRequest.of(page,size))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Addresses fetched", addresses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal final User user,
            @Valid @RequestBody final AddressRequest request) {

        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .street(request.getStreet())
                .city(request.getCity())
                .stateName(request.getStateName())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .build();

        Address saved = addressRepository.save(address);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address saved", toResponse(saved)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal final User user,
            @PathVariable final Integer id) {

        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        addressRepository.delete(address);
        return ResponseEntity.ok(ApiResponse.success("Address deleted", null));
    }

    private @NotNull AddressResponse toResponse(@NotNull final Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .street(address.getStreet())
                .city(address.getCity())
                .stateName(address.getStateName())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();
    }
}