package com.product.hms.service.impl;

import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.response.PaymentResponse;
import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.PaymentAllocationEntity;
import com.product.hms.entity.PaymentTransactionEntity;
import com.product.hms.enums.FolioItemStatus;
import com.product.hms.enums.PaymentMethod;
import com.product.hms.enums.PaymentTransactionStatus;
import com.product.hms.enums.PaymentTransactionType;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.repository.FolioItemRepository;
import com.product.hms.repository.FolioRepository;
import com.product.hms.repository.PaymentAllocationRepository;
import com.product.hms.repository.PaymentTransactionRepository;
import com.product.hms.service.PaymentService;
import com.product.hms.utils.VnPayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final FolioRepository folioRepository;
    private final FolioItemRepository folioItemRepository;
    private final PaymentAllocationRepository paymentAllocationRepository;
    private final VnPayUtil vnPayUtil;

    @Value("${vnpay.return-url:http://localhost:8080/api/v1/payment/vnpay-ipn}")
    private String vnPayReturnUrl;

    @Override
    @Transactional
    public String createVnPayPaymentUrl(Long folioId,
                                        BigDecimal amount,
                                        String clientIp) {
        FolioEntity folio = folioRepository.findById(folioId)
                .orElseThrow(() -> new IllegalArgumentException("Folio not found with id " + folioId));

        PaymentTransactionEntity transaction = new PaymentTransactionEntity();
        transaction.setFolioEntity(folio);
        transaction.setCode(generateTransactionCode());
        String txnRef = generateTxnRef();
        transaction.setTransactionReference(txnRef);
        transaction.setPaymentMethod(PaymentMethod.VNPAY.getDbValue());
        transaction.setAmount(amount);
        transaction.setType(PaymentTransactionType.PAYMENT.getDbValue());
        transaction.setStatus(PaymentTransactionStatus.PENDING.getDbValue());
        transaction.setCreatedAt(Timestamp.from(Instant.now()));
        transaction.setIsActive(true);

        paymentTransactionRepository.save(transaction);

        long amountVnd = amount.longValue();
        String orderInfo = "Payment for folio " + folio.getId();
        return vnPayUtil.generatePaymentUrl(txnRef, amountVnd, safeClientIp(clientIp), orderInfo, vnPayReturnUrl);
    }

    @Override
    @Transactional
    public String createVnPaymentUrlByPaymentTransactionId(long paymentTransactionId,
                                                           String clientIp) {
        PaymentTransactionEntity transaction = paymentTransactionRepository.findById(paymentTransactionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment transaction not found with id " + paymentTransactionId));

        if (PaymentTransactionStatus.SUCCESS.getDbValue().equalsIgnoreCase(transaction.getStatus())) {
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
        return vnPayUtil.generatePaymentUrl(txnRef, amountVnd, safeClientIp(clientIp), orderInfo, vnPayReturnUrl);
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
            if (!PaymentTransactionStatus.PENDING.getDbValue().equalsIgnoreCase(transaction.getStatus())) {
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            // 5. Xử lý logic nghiệp vụ khi thanh toán thành công
            if ("00".equals(vnpResponseCode)) {
                transaction.setStatus(PaymentTransactionStatus.SUCCESS.getDbValue());

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
                transaction.setStatus(PaymentTransactionStatus.FAILED.getDbValue());
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

    @Override
    @Transactional
    public PaymentResponse processPaymentForFolio(FolioEntity folio, PaymentRequest request) {
        if (request.folioItemIds() == null || request.folioItemIds().isEmpty()) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Folio item IDs must not be empty");
        }

        BigDecimal depositAmount = request.depositAmount() != null ? request.depositAmount() : BigDecimal.ZERO;
        if (depositAmount.signum() < 0) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Deposit amount must be >= 0");
        }

        List<FolioItemEntity> selectedItems = folioItemRepository.findAllById(request.folioItemIds());
        if (selectedItems.size() != request.folioItemIds().size()) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Some folio items not found");
        }

        for (FolioItemEntity item : selectedItems) {
            if (!item.getFolioEntity().getId().equals(folio.getId())) {
                throw new BadRequestException(
                        ErrorCode.INVALID_REQUEST,
                        "Folio item " + item.getId() + " does not belong to this folio"
                );
            }
            if (item.getStatus() != FolioItemStatus.UNPAID) {
                throw new BadRequestException(
                        ErrorCode.INVALID_REQUEST,
                        "Folio item " + item.getId() + " is not in UNPAID status"
                );
            }
        }

        BigDecimal selectedItemsTotal = selectedItems.stream()
                .map(FolioItemEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (depositAmount.compareTo(selectedItemsTotal) > 0) {
            throw new BadRequestException(
                    ErrorCode.INVALID_REQUEST,
                    "Deposit amount cannot exceed selected folio items total"
            );
        }

        BigDecimal cashCollected = selectedItemsTotal.subtract(depositAmount);

        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.paymentMethod());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Invalid payment method: " + request.paymentMethod());
        }

        PaymentTransactionEntity transaction = new PaymentTransactionEntity();
        transaction.setFolioEntity(folio);
        transaction.setCode(generateTransactionCode());
        transaction.setPaymentMethod(paymentMethod.getDbValue());
        transaction.setAmount(cashCollected);
        transaction.setType(PaymentTransactionType.PAYMENT.getDbValue());
        transaction.setStatus(paymentMethod == PaymentMethod.VNPAY
                ? PaymentTransactionStatus.PENDING.getDbValue()
                : PaymentTransactionStatus.SUCCESS.getDbValue());
        transaction.setCreatedAt(Timestamp.from(Instant.now()));
        transaction.setIsActive(true);
        paymentTransactionRepository.save(transaction);

        for (FolioItemEntity item : selectedItems) {
            PaymentAllocationEntity allocation = new PaymentAllocationEntity();
            allocation.setPaymentTransactionEntity(transaction);
            allocation.setFolioItemEntity(item);
            allocation.setAmountApplied(item.getTotalPrice());
            allocation.setIsActive(true);
            paymentAllocationRepository.save(allocation);

            if (paymentMethod != PaymentMethod.VNPAY) {
                item.setStatus(FolioItemStatus.PAID);
                folioItemRepository.save(item);
            }
        }

        BigDecimal newBalance = folio.getBalance();
        if (paymentMethod != PaymentMethod.VNPAY) {
            BigDecimal newTotalPaid = folio.getTotalPaid().add(selectedItemsTotal);
            newBalance = folio.getTotalCharges().subtract(newTotalPaid);
            folio.setTotalPaid(newTotalPaid);
            folio.setBalance(newBalance);
            folioRepository.save(folio);
        }

        String paymentUrl = null;
        if (paymentMethod == PaymentMethod.VNPAY) {
            paymentUrl = createVnPaymentUrlByPaymentTransactionId(
                    transaction.getId(),
                    request.clientIp()
            );
        }

        return new PaymentResponse(
                transaction.getId(),
                transaction.getCode(),
                paymentMethod.getDbValue(),
                selectedItemsTotal,
                depositAmount,
                cashCollected,
                transaction.getStatus(),
                newBalance,
                paymentUrl,
                transaction.getCreatedAt()
        );
    }

    private String safeClientIp(String clientIp) {
        return (clientIp == null || clientIp.isBlank()) ? "127.0.0.1" : clientIp;
    }

    private String generateTransactionCode() {
        return "PAY-" + UUID.randomUUID();
    }

    private String generateTxnRef() {
        return String.valueOf(System.currentTimeMillis());
    }
}

