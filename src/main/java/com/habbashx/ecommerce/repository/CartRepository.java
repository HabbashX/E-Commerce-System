package com.habbashx.ecommerce.repository;

import com.habbashx.ecommerce.entity.Cart;
import com.habbashx.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {

    Optional<Cart> findByUser(User user);

}
