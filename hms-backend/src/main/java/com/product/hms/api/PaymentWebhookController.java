package com.product.hms.api;

import com.product.hms.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentWebhookController {

    private final PaymentService paymentService;

    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> handleVnPayIpn(@RequestParam Map<String, String> allParams) {
        Map<String, String> result = paymentService.processVnPayIpn(allParams);

        return ResponseEntity.ok(result);
    }
}

