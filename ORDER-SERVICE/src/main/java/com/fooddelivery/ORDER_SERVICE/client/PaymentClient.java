package com.fooddelivery.ORDER_SERVICE.client;

import com.fooddelivery.ORDER_SERVICE.dto.PaymentRequest;
import com.fooddelivery.ORDER_SERVICE.dto.PaymentResponse;
import com.fooddelivery.ORDER_SERVICE.security.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "PAYMENT-SERVICE",
        configuration = FeignConfig.class
)
public interface PaymentClient {

    @PostMapping("/api/payments")
    PaymentResponse processPayment(
            @RequestBody PaymentRequest paymentRequest
    );
}