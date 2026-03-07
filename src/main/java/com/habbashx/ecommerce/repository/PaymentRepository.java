package com.habbashx.ecommerce.repository;

import com.habbashx.ecommerce.entity.Order;
import com.habbashx.ecommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {


    Optional<Payment> findByOrder(Order order);

    boolean existsByOrder(Order order);

}
