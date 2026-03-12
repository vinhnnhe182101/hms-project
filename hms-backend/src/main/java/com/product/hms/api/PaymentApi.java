package com.product.hms.api;

import com.product.hms.dto.response.PaymentResponse;
import com.product.hms.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentApi {
    private final PaymentService paymentService;

    /**
     * Đánh dấu giao dịch thanh toán offline là đã thanh toán (chỉ cho phép với phương thức CASH, chưa thành công).
     * @param paymentTransactionId ID giao dịch thanh toán
     * @return PaymentResponse
     */
    @PostMapping("/{paymentTransactionId}/mark-as-paid")
    public ResponseEntity<PaymentResponse> markAsPaidOffline(@PathVariable Long paymentTransactionId) {
        PaymentResponse response = paymentService.markAsPaid(paymentTransactionId);
        return ResponseEntity.ok(response);
    }
}
