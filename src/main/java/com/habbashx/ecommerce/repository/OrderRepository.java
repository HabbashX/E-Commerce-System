package com.habbashx.ecommerce.repository;

import com.habbashx.ecommerce.entity.Order;
import com.habbashx.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {

    Page<Order> findOrderByUser(User user , Pageable pageable);

    Optional<Order> findByIdAndUser(Integer id, User user);

}
