package com.habbashx.ecommerce.repository;

import com.habbashx.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {

    Page<Product> findProductByProductNameContainingIgnoreCaseAndIsActiveTrue(String productName, Pageable pageable);

    @Query("""
            SELECT p from Product p WHERE p.isActive = true
            """)
    Page<Product> findProductByIsActiveTrue(Boolean isActive ,Pageable pageable);

    @Query("""
            SELECT p FROM Product p JOIN p.categories c
            WHERE c.id = :categoryId AND p.isActive = true
            """)
    Page<Product> findByCategoryId(Integer id , Pageable pageable);


    @Query("""
            SELECT p FROM Product p WHERE p.isActive = true
                      AND(:minPrice IS NULL OR p.price >= :minPrice)
                      AND(:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    Page<Product> findByPriceRange(
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minPrice") BigDecimal minPrice,
            Pageable pageable
            );


    @Query("""
            SELECT DISTINCT p FROM Product p
            JOIN p.categories c
            WHERE p.isActive = true
            AND (:name IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:categoryId IS NULL OR c.id = :categoryId)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    Page<Product> findWithFilters(
            @Param("name")       String name,
            @Param("categoryId") Integer categoryId,
            @Param("minPrice")   java.math.BigDecimal minPrice,
            @Param("maxPrice")   java.math.BigDecimal maxPrice,
            Pageable pageable);

    Optional<Product> findProductById(Integer id);
}
