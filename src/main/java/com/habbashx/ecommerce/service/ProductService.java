package com.habbashx.ecommerce.service;

import com.habbashx.ecommerce.dto.response.CategoryResponse;
import com.habbashx.ecommerce.dto.request.ProductRequest;
import com.habbashx.ecommerce.dto.response.ProductResponse;
import com.habbashx.ecommerce.entity.Category;
import com.habbashx.ecommerce.entity.Product;
import com.habbashx.ecommerce.repository.CategoryRepository;
import com.habbashx.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProduct(Pageable pageable) {
        return productRepository.findProductByIsActiveTrue(true,pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(final Integer id) {
        final Product product =findProductById(id);
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchByName(final String productName,final Pageable pageable) {
        return productRepository.findProductByProductNameContainingIgnoreCaseAndIsActiveTrue(productName,pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getByCategory(final Integer categoryId ,final Pageable pageable) {
        return productRepository.findByCategoryId(categoryId,pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> filterProducts(
            String name,
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {

        return productRepository.findWithFilters(name,categoryId,minPrice,maxPrice,pageable).map(this::toResponse);
    }


    @Transactional
    public ProductResponse createProduct(@NotNull final ProductRequest request) {
        Set<Category> categories = resolveCategories(request.getCategoryIds());

        Product product = Product.builder()
                .productName(request.getProductName())
                .productDescription(request.getProductDesc())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .categories(categories)
                .build();

        return toResponse(productRepository.save(product));
    }


    @Transactional
    public ProductResponse updateProduct(@NotNull final Integer id , @NotNull final ProductRequest productRequest) {
        Product product = findProductById(id);

        product.setProductName(productRequest.getProductName());
        product.setProductDescription(productRequest.getProductDesc());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());

        if (productRequest.getIsActive() != null) {
            product.setIsActive(productRequest.getIsActive());
        }
        if (productRequest.getCategoryIds() != null) {
            product.setCategories(resolveCategories(productRequest.getCategoryIds()));
        }

        return toResponse(productRepository.save(product));

    }

    @Transactional
    public void deleteProduct(Integer id) {
        Product product = findProductById(id);
        product.setIsActive(false);
        productRepository.save(product);
    }

    private Set<Category> resolveCategories(Set<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return new HashSet<>();
        return categoryIds.stream()
                .map(catId -> categoryRepository.findById(catId)
                        .orElseThrow(() -> new RuntimeException("Category not found with id: " + catId)))
                .collect(Collectors.toSet());
    }


    private ProductResponse toResponse(@NotNull final Product product) {

        Set<CategoryResponse> categoryResponses = product.getCategories()
                .stream()
                .map(cat -> CategoryResponse.builder()
                        .id(cat.getId())
                        .categoryName(cat.getCategoryName())
                        .build()).collect(Collectors.toSet());

        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productDesc(product.getProductDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .isActive(product.getIsActive())
                .categories(categoryResponses)
                .build();
    }

    private @NotNull Product findProductById(@NotNull final Integer id) {
        return productRepository.findProductById(id).orElseThrow(() ->  new RuntimeException("product not found"));
    }
}
