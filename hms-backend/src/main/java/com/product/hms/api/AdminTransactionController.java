package com.product.hms.api;

import com.product.hms.service.TransactionService;
import com.product.hms.dto.response.PaymentTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<?> getAllTransactions(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long folioId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(transactionService.getAllTransactions(code, paymentMethod, type, status, folioId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentTransactionResponse> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
}
