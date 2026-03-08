package com.product.hms.service.impl;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.PaymentTransactionEntity;
import com.product.hms.repository.FolioRepository;
import com.product.hms.repository.PaymentTransactionRepository;
import com.product.hms.service.PaymentService;
import com.product.hms.utils.VnPayUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final FolioRepository folioRepository;
    private final VnPayUtil vnPayUtil;

    public PaymentServiceImpl(PaymentTransactionRepository paymentTransactionRepository,
                              FolioRepository folioRepository,
                              VnPayUtil vnPayUtil) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.folioRepository = folioRepository;
        this.vnPayUtil = vnPayUtil;
    }

    @Override
    @Transactional
    public String createVnPayPaymentUrl(Long folioId,
                                        BigDecimal amount,
                                        String clientIp,
                                        String returnUrl) {
        FolioEntity folio = folioRepository.findById(folioId)
                .orElseThrow(() -> new IllegalArgumentException("Folio not found with id " + folioId));

        PaymentTransactionEntity transaction = new PaymentTransactionEntity();
        transaction.setFolioEntity(folio);
        transaction.setCode(generateTransactionCode());
        String txnRef = generateTxnRef();
        transaction.setTransactionReference(txnRef);
        transaction.setPaymentMethod("VNPAY");
        transaction.setAmount(amount);
        transaction.setType("PAYMENT");
        transaction.setStatus("PENDING");
        transaction.setCreatedAt(java.sql.Timestamp.from(Instant.now()));

        paymentTransactionRepository.save(transaction);

        long amountVnd = amount.longValue();
        String orderInfo = "Payment for folio " + folio.getId();

        return vnPayUtil.generatePaymentUrl(txnRef, amountVnd, clientIp, orderInfo, returnUrl);
    }

    @Override
    @Transactional
    public String createVnPaymentUrlByPaymentTransactionId(long paymentTransactionId,
                                                           String clientIp,
                                                           String returnUrl) {
        PaymentTransactionEntity transaction = paymentTransactionRepository.findById(paymentTransactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment transaction not found with id " + paymentTransactionId));

        if ("SUCCESS".equalsIgnoreCase(transaction.getStatus())) {
            throw new IllegalStateException("Transaction is already paid");
        }

        if (transaction.getAmount() == null) {
            throw new IllegalArgumentException("Payment transaction amount is missing for id " + paymentTransactionId);
        }

        long amountVnd = transaction.getAmount().longValue();

        String txnRef = transaction.getTransactionReference();
        if (txnRef == null || txnRef.isBlank()) {
            txnRef = generateTxnRef();
            transaction.setTransactionReference(txnRef);
            paymentTransactionRepository.save(transaction);
        }

        String orderInfo = "Payment for transaction " + paymentTransactionId;

        return vnPayUtil.generatePaymentUrl(txnRef, amountVnd, clientIp, orderInfo, returnUrl);
    }

    @Override
@Transactional
public Map<String, String> processVnPayIpn(Map<String, String> params) {
    Map<String, String> response = new HashMap<>();
    try {
        // 1. Kiểm tra chữ ký (Checksum)
        if (!vnPayUtil.validateSignature(params)) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid signature");
            return response;
        }

        String vnpTxnRef = params.get("vnp_TxnRef");
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String amountStr = params.get("vnp_Amount");

        // 2. Kiểm tra sự tồn tại của giao dịch trong DB
        // Sử dụng findByTransactionReference có kèm PESSIMISTIC_WRITE để tránh Race Condition
        Optional<PaymentTransactionEntity> optionalTransaction =
                paymentTransactionRepository.findByTransactionReference(vnpTxnRef);

        if (optionalTransaction.isEmpty()) {
            response.put("RspCode", "01");
            response.put("Message", "Order not found");
            return response;
        }

        PaymentTransactionEntity transaction = optionalTransaction.get();

        // 3. Kiểm tra số tiền (VNPAY nhân 100 lần số tiền thực tế)
        long vnpAmount = Long.parseLong(amountStr) / 100;
        if (transaction.getAmount().longValue() != vnpAmount) {
            response.put("RspCode", "04");
            response.put("Message", "Invalid amount");
            return response;
        }

        // 4. Kiểm tra trạng thái giao dịch (Idempotency)
        // Nếu đã khác PENDING nghĩa là đã được xử lý bởi lần gọi IPN trước hoặc Return URL
        if (!"PENDING".equals(transaction.getStatus())) {
            response.put("RspCode", "02");
            response.put("Message", "Order already confirmed");
            return response;
        }

        // 5. Xử lý logic nghiệp vụ khi thanh toán thành công
        if ("00".equals(vnpResponseCode)) {
            transaction.setStatus("SUCCESS");
            
            FolioEntity folio = transaction.getFolioEntity();
            if (folio != null) {
                // Cập nhật số tiền đã trả và số dư
                BigDecimal paidAmount = BigDecimal.valueOf(vnpAmount);
                BigDecimal currentTotalPaid = folio.getTotalPaid() != null ? folio.getTotalPaid() : BigDecimal.ZERO;
                
                folio.setTotalPaid(currentTotalPaid.add(paidAmount));
                
                BigDecimal totalCharges = folio.getTotalCharges() != null ? folio.getTotalCharges() : BigDecimal.ZERO;
                folio.setBalance(totalCharges.subtract(folio.getTotalPaid()));
                
                folioRepository.save(folio);
            }
        } else {
            transaction.setStatus("FAILED");
        }

        paymentTransactionRepository.save(transaction);

        // 6. Phản hồi thành công cho VNPAY
        response.put("RspCode", "00");
        response.put("Message", "Confirm Success");

    } catch (Exception e) {
        response.put("RspCode", "99");
        response.put("Message", "Unknown error: " + e.getMessage());
    }
    
    return response;
}

    private String generateTransactionCode() {
        return "PAY-" + UUID.randomUUID();
    }

    private String generateTxnRef() {
        return String.valueOf(System.currentTimeMillis());
    }
}

