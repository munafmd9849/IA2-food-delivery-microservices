package com.fooddelivery.PAYMENT_SERVICE.controller;

import com.fooddelivery.PAYMENT_SERVICE.dto.PaymentRequest;
import com.fooddelivery.PAYMENT_SERVICE.dto.PaymentResponse;
import com.fooddelivery.PAYMENT_SERVICE.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @RequestBody PaymentRequest request) {

        return ResponseEntity.ok(
                paymentService.processPayment(request)
        );
    }
}