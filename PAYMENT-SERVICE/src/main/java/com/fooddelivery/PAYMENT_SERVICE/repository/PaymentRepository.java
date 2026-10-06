package com.fooddelivery.PAYMENT_SERVICE.repository;

import com.fooddelivery.PAYMENT_SERVICE.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}