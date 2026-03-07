package com.habbashx.ecommerce.controller;

import com.habbashx.ecommerce.dto.response.ApiResponse;
import com.habbashx.ecommerce.dto.request.ProductRequest;
import com.habbashx.ecommerce.dto.response.ProductResponse;
import com.habbashx.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") final Integer page,
            @RequestParam(defaultValue = "10") final Integer size,
            @RequestParam(defaultValue = "id") final String sortBy,
            @RequestParam(defaultValue = "asc") final String direction
    ) {

        final Pageable pageable = buildPageable(page,size,sortBy,direction);

        return ResponseEntity.ok(
                ApiResponse.success("Products fetched",
                        productService.getAllProduct(pageable))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable final Integer id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success("Search Results: ",
                        productService.getProductById(id))
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchByName(
            @RequestParam final String name,
            @RequestParam(defaultValue = "0") final Integer page,
            @RequestParam(defaultValue = "10") final Integer size
    ) {
        final Pageable pageable = PageRequest.of(page,size);

        return ResponseEntity.ok(
                ApiResponse.success("Search Results ",
                        productService.searchByName(name,pageable))
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getByCategory(
            @PathVariable final Integer categoryId,
            @RequestParam(defaultValue = "0") final Integer page,
            @RequestParam(defaultValue = "10") final Integer size
    ) {

        Pageable pageable = PageRequest.of(page,size);

        return ResponseEntity.ok(
                ApiResponse.success("Products by category"
                        ,productService.getByCategory(categoryId,pageable)
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody final ProductRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created", productService.createProduct(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable final Integer id,
            @Valid @RequestBody final ProductRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Product updated", productService.updateProduct(id, request))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable final Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }


    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> filterProducts(
            @RequestParam(required = false) final String name,
            @RequestParam(required = false) final Integer categoryId,
            @RequestParam(required = false)  BigDecimal min,
            @RequestParam(required = false)  BigDecimal max,
            @RequestParam(defaultValue = "0") final Integer page,
            @RequestParam(defaultValue = "10") final Integer size

    ) {

        Pageable pageable = PageRequest.of(page,size);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "filtered products",productService.filterProducts(name,categoryId,min,max,pageable)
                )
        );
    }

    private @NotNull Pageable buildPageable(final int page, final int size, final String sortBy, final String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return PageRequest.of(page,size,sort);
    }
}
