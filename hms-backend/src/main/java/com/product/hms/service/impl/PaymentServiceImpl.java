package com.product.hms.service.impl;

import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.response.PaymentResponse;
import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.PaymentAllocationEntity;
import com.product.hms.entity.PaymentTransactionEntity;
import com.product.hms.enums.PaymentMethod;
import com.product.hms.enums.PaymentTransactionStatus;
import com.product.hms.enums.PaymentTransactionType;
import com.product.hms.repository.FolioItemRepository;
import com.product.hms.repository.FolioRepository;
import com.product.hms.repository.PaymentAllocationRepository;
import com.product.hms.repository.PaymentTransactionRepository;
import com.product.hms.service.FolioItemService;
import com.product.hms.service.FolioService;
import com.product.hms.service.PaymentAllocationService;
import com.product.hms.service.PaymentService;
import com.product.hms.service.impl.payment.PaymentValidationSupport;
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
    private final PaymentAllocationService paymentAllocationService;
    private final FolioService folioService;
    private final FolioItemService folioItemService;

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
    public PaymentResponse markAsPaid(Long paymentTransactionId) {
        // STEP 1: Kiểm tra payment transaction
        PaymentTransactionEntity paymentTransactionEntity = PaymentValidationSupport.validatePaymentTransactionExistence(paymentTransactionId, paymentTransactionRepository);
        PaymentValidationSupport.validatePaymentTransactionNotCompleted(paymentTransactionEntity);
        PaymentValidationSupport.validatePaymentTransactionMethod(paymentTransactionEntity, PaymentMethod.CASH);

        // STEP 2: Cập nhật trạng thái của payment transaction thành SUCCESS
        paymentTransactionEntity.setStatus(PaymentTransactionStatus.SUCCESS.getDbValue());
        paymentTransactionRepository.save(paymentTransactionEntity);

        // STEP 3: Đánh dấu các folio item liên quan đến payment allocation là PAID
        List<FolioItemEntity> items = paymentTransactionEntity.getPaymentAllocationEntities().stream()
                .map(PaymentAllocationEntity::getFolioItemEntity)
                .toList();
        folioItemService.markItemsAsPaid(items);

        // STEP 4: Cập nhật tổng đã trả và số dư của folio qua service
        FolioEntity folio = paymentTransactionEntity.getFolioEntity();
        if (folio != null) {
            BigDecimal paidAmount = paymentTransactionEntity.getAmount() != null ? paymentTransactionEntity.getAmount() : BigDecimal.ZERO;
            folioService.addPaidAmount(folio, paidAmount);
        }

        return new PaymentResponse(
                paymentTransactionEntity.getId(),
                paymentTransactionEntity.getCode(),
                paymentTransactionEntity.getPaymentMethod(),
                paymentTransactionEntity.getAmount(),
                BigDecimal.ZERO, // depositAmount
                paymentTransactionEntity.getAmount(), // cashCollected
                paymentTransactionEntity.getStatus(),
                null, // paymentUrl
                paymentTransactionEntity.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public PaymentResponse processPaymentForFolio(FolioEntity folioEntity, PaymentRequest paymentRequest) {
        // STEP 1: Kiểm tra tính hợp lệ của request
        BigDecimal depositAmount = PaymentValidationSupport.validateDepositAmountNegative(paymentRequest.depositAmount());
        PaymentValidationSupport.validateFolioItemIdsNullOrEmpty(paymentRequest.folioItemIds());

        List<FolioItemEntity> selectedFolioItemEntities = PaymentValidationSupport.validateFolioItemsExistence(paymentRequest.folioItemIds(), folioItemRepository);

        PaymentValidationSupport.validateFolioItemsBelongToFolio(folioEntity, selectedFolioItemEntities);
        PaymentValidationSupport.validateFolioItemNotPaid(selectedFolioItemEntities);

        BigDecimal selectedItemsTotalPrice = PaymentValidationSupport.validateDepositAmountNotExceedTotal(selectedFolioItemEntities, depositAmount);
        BigDecimal cashCollected = selectedItemsTotalPrice.subtract(depositAmount);

        PaymentMethod paymentMethod = PaymentValidationSupport.validatePaymentMethodExistence(paymentRequest.paymentMethod());

        // STEP 2: Tạo payment transaction
        PaymentTransactionEntity paymentTransactionEntity = cretePaymentTransaction(folioEntity, paymentMethod, cashCollected);

        // STEP 3: Tạo payment allocation cho từng folio item đã chọn
        paymentAllocationService.createPaymentAllocation(paymentTransactionEntity, selectedFolioItemEntities);

        // TODO: Cần xem xét lại vì dù method nào cũng sẽ cần gọi api để đánh dấu đã thanh toán thành công, lúc đó với cập nhật tổng đã trả và số dư.
//        BigDecimal newBalance = folioEntity.getBalance();
//        if (paymentMethod != PaymentMethod.VNPAY) {
//            BigDecimal newTotalPaid = folioEntity.getTotalPaid().add(selectedItemsTotalPrice);
//            newBalance = folioEntity.getTotalCharges().subtract(newTotalPaid);
//            folioEntity.setTotalPaid(newTotalPaid);
//            folioEntity.setBalance(newBalance);
//            folioRepository.save(folioEntity);
//        }

        // STEP 4: Nếu là VNPAY thì tạo URL thanh toán
        String paymentUrl = null;
        if (paymentMethod == PaymentMethod.VNPAY) {
            paymentUrl = createVnPaymentUrlByPaymentTransactionId(
                    paymentTransactionEntity.getId(),
                    paymentRequest.clientIp()
            );
        }

        return new PaymentResponse(
                paymentTransactionEntity.getId(),
                paymentTransactionEntity.getCode(),
                paymentMethod.getDbValue(),
                selectedItemsTotalPrice,
                depositAmount,
                cashCollected,
                paymentTransactionEntity.getStatus(),
                paymentUrl,
                paymentTransactionEntity.getCreatedAt()
        );
    }

    private PaymentTransactionEntity cretePaymentTransaction(FolioEntity folioEntity, PaymentMethod paymentMethod, BigDecimal cashCollected) {
        PaymentTransactionEntity paymentTransactionEntity = new PaymentTransactionEntity();
        paymentTransactionEntity.setFolioEntity(folioEntity);
        paymentTransactionEntity.setCode(generateTransactionCode());
        paymentTransactionEntity.setPaymentMethod(paymentMethod.getDbValue());
        paymentTransactionEntity.setAmount(cashCollected);
        paymentTransactionEntity.setType(PaymentTransactionType.PAYMENT.getDbValue());
        paymentTransactionEntity.setStatus(PaymentTransactionStatus.PENDING.getDbValue());
        paymentTransactionEntity.setCreatedAt(Timestamp.from(Instant.now()));
        paymentTransactionEntity.setIsActive(true);
        paymentTransactionRepository.save(paymentTransactionEntity);
        return paymentTransactionEntity;
    }

    /**
     * Đảm bảo client IP không null hoặc blank để tránh lỗi khi gọi VNPAY API. Nếu client IP không hợp lệ, mặc định trả về "".
     *
     * @param clientIp địa chỉ IP của client, có thể null hoặc blank
     * @return client IP hợp lệ, nếu input không hợp lệ sẽ trả về ""
     */
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

