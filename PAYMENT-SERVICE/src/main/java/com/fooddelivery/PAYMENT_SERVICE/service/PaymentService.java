package com.fooddelivery.PAYMENT_SERVICE.service;

import com.fooddelivery.PAYMENT_SERVICE.dto.PaymentRequest;
import com.fooddelivery.PAYMENT_SERVICE.dto.PaymentResponse;
import com.fooddelivery.PAYMENT_SERVICE.entity.Payment;
import com.fooddelivery.PAYMENT_SERVICE.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse processPayment(PaymentRequest request) {

        String status;

        if (request.getAmount() == null || request.getAmount() <= 0) {
            status = "FAILED";
        } else {
            status = "SUCCESS";
        }

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .paymentStatus(status)
                .paymentDate(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return new PaymentResponse(
                savedPayment.getPaymentId(),
                savedPayment.getOrderId(),
                savedPayment.getAmount(),
                savedPayment.getPaymentStatus(),
                savedPayment.getPaymentDate().toString()
        );
    }
}