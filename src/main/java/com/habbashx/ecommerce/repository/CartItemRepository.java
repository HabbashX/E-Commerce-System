package com.habbashx.ecommerce.repository;

import com.habbashx.ecommerce.entity.Cart;
import com.habbashx.ecommerce.entity.CartItem;
import com.habbashx.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {

    Optional<CartItem> findByCartAndProduct(Cart cart , Product product);

}
