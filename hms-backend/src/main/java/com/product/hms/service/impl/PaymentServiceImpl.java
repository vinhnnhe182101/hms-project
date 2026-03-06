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
    public void processVnPayIpn(Map<String, String> params) {
        boolean validSignature = vnPayUtil.validateSignature(params);
        if (!validSignature) {
            throw new IllegalArgumentException("Invalid VNPAY signature");
        }

        String vnpTxnRef = params.get("vnp_TxnRef");
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String vnpTransactionStatus = params.get("vnp_TransactionStatus");
        String amountStr = params.get("vnp_Amount");

        if (vnpTxnRef == null) {
            throw new IllegalArgumentException("Missing vnp_TxnRef");
        }

        Optional<PaymentTransactionEntity> optionalTransaction =
                paymentTransactionRepository.findByTransactionReference(vnpTxnRef);

        PaymentTransactionEntity transaction = optionalTransaction
                .orElseThrow(() -> new IllegalArgumentException("Payment transaction not found for reference " + vnpTxnRef));

        boolean isSuccess = "00".equals(vnpResponseCode) && "00".equals(vnpTransactionStatus);

        if (isSuccess) {
            transaction.setStatus("SUCCESS");

            if (amountStr != null) {
                try {
                    long amountLong = Long.parseLong(amountStr) / 100;
                    BigDecimal amount = BigDecimal.valueOf(amountLong);

                    FolioEntity folio = transaction.getFolioEntity();
                    if (folio != null) {
                        BigDecimal currentPaid = folio.getTotalPaid() != null ? folio.getTotalPaid() : BigDecimal.ZERO;
                        BigDecimal newTotalPaid = currentPaid.add(amount);
                        folio.setTotalPaid(newTotalPaid);

                        BigDecimal totalCharges = folio.getTotalCharges() != null ? folio.getTotalCharges() : BigDecimal.ZERO;
                        BigDecimal newBalance = totalCharges.subtract(newTotalPaid);
                        folio.setBalance(newBalance);

                        folioRepository.save(folio);
                    }
                } catch (NumberFormatException ignored) {
                }
            }

        } else {
            transaction.setStatus("FAILED");
        }

        paymentTransactionRepository.save(transaction);
    }

    private String generateTransactionCode() {
        return "PAY-" + UUID.randomUUID();
    }

    private String generateTxnRef() {
        return String.valueOf(System.currentTimeMillis());
    }
}

