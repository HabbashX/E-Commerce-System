package com.habbashx.ecommerce.controller;

import com.habbashx.ecommerce.dto.response.ApiResponse;
import com.habbashx.ecommerce.dto.response.UserInformationResponse;
import com.habbashx.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserInformationResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") final Integer page,
            @RequestParam(defaultValue = "10") final Integer size,
            @RequestParam(defaultValue = "id") final String sortBy,
            @RequestParam(defaultValue = "asc") final String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                ApiResponse.success("Users fetched", userService.getAllUsers(pageable))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserInformationResponse>> getUserById(
            @PathVariable final Integer id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("User fetched", userService.getUserById(id))
        );
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserInformationResponse>> getUserByUsername(
            @PathVariable final String username
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("User fetched", userService.getUserByUsername(username))
        );
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserInformationResponse>> getUserByEmail(
            @PathVariable final String email
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("User fetched", userService.getUserByEmail(email))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable final Integer id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }
}