package com.habbashx.ecommerce.repository;

import com.habbashx.ecommerce.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {

    Optional<Category> findCategoryByCategoryName(String categoryName);

    boolean existsByCategoryName(String categoryName);

    Page<Category> findCategoryById(Integer id, Pageable pageable);

}
