package com.product.hms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.response.PaymentResponse;
import com.product.hms.entity.*;
import com.product.hms.enums.PaymentMethod;
import com.product.hms.enums.PaymentTransactionStatus;
import com.product.hms.enums.PaymentTransactionType;
import com.product.hms.enums.ReservationRoomStatus;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.repository.*;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.ErrorCode;
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
    private final VnPayUtil vnPayUtil;
    private final PaymentAllocationService paymentAllocationService;
    private final FolioService folioService;
    private final FolioItemService folioItemService;
    private final ReservationRoomRepository reservationRoomRepository;
    private final VnPayTransactionDetailRepository vnPayTransactionDetailRepository;
    private final ObjectMapper objectMapper;
    private final ReservationRepository reservationRepository;

    @Value("${vnpay.return-url}")
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
            Optional<PaymentTransactionEntity> optionalTransaction =
                    paymentTransactionRepository.findByTransactionReference(vnpTxnRef);

            if (optionalTransaction.isEmpty()) {
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
                return response;
            }

            PaymentTransactionEntity transaction = optionalTransaction.get();

            // 3. Kiểm tra số tiền
            long vnpAmount = Long.parseLong(amountStr) / 100;
            if (transaction.getAmount().longValue() != vnpAmount) {
                response.put("RspCode", "04");
                response.put("Message", "Invalid amount");
                return response;
            }

            // 4. Kiểm tra trạng thái giao dịch
            if (!PaymentTransactionStatus.PENDING.getDbValue().equalsIgnoreCase(transaction.getStatus())) {
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            // 5. Xử lý logic nghiệp vụ
            if ("00".equals(vnpResponseCode)) {
                handleSuccessfulPayment(transaction, vnpAmount);
            } else {
                handleFailedPayment(transaction);
            }

            // Lưu transaction chính
            paymentTransactionRepository.save(transaction);

            // Lưu chi tiết VNPAY
            saveVnPayDetail(transaction, params);

            // 6. Phản hồi thành công cho VNPAY
            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");
            response.put("vnp_ResponseCode", vnpResponseCode);

        } catch (Exception e) {
            response.put("RspCode", "99");
            response.put("Message", "Unknown error: " + e.getMessage());
        }

        return response;
    }

    @Override
    public PaymentResponse markAsPaid(Long paymentTransactionId) {
        PaymentTransactionEntity paymentTransactionEntity = PaymentValidationSupport.validatePaymentTransactionExistence(paymentTransactionId, paymentTransactionRepository);
        PaymentValidationSupport.validatePaymentTransactionNotCompleted(paymentTransactionEntity);
        PaymentValidationSupport.validatePaymentTransactionMethod(paymentTransactionEntity, PaymentMethod.CASH);

        paymentTransactionEntity.setStatus(PaymentTransactionStatus.SUCCESS.getDbValue());
        paymentTransactionRepository.save(paymentTransactionEntity);

        List<FolioItemEntity> items = paymentTransactionEntity.getPaymentAllocationEntities().stream()
                .map(PaymentAllocationEntity::getFolioItemEntity)
                .toList();
        folioItemService.markItemsAsPaid(items);

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
                BigDecimal.ZERO,
                paymentTransactionEntity.getAmount(),
                paymentTransactionEntity.getStatus(),
                null,
                paymentTransactionEntity.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public PaymentResponse processPaymentForFolio(FolioEntity folioEntity, PaymentRequest paymentRequest) {
        BigDecimal depositAmount = PaymentValidationSupport.validateDepositAmountNegative(paymentRequest.depositAmount());
        PaymentValidationSupport.validateFolioItemIdsNullOrEmpty(paymentRequest.folioItemIds());

        List<FolioItemEntity> selectedFolioItemEntities = PaymentValidationSupport.validateFolioItemsExistence(paymentRequest.folioItemIds(), folioItemRepository);

        PaymentValidationSupport.validateFolioItemsBelongToFolio(folioEntity, selectedFolioItemEntities);
        PaymentValidationSupport.validateFolioItemNotPaid(selectedFolioItemEntities);

        BigDecimal selectedItemsTotalPrice = PaymentValidationSupport.validateDepositAmountNotExceedTotal(selectedFolioItemEntities, depositAmount);
        BigDecimal cashCollected = selectedItemsTotalPrice.subtract(depositAmount);

        PaymentMethod paymentMethod = PaymentValidationSupport.validatePaymentMethodExistence(paymentRequest.paymentMethod());

        PaymentTransactionEntity paymentTransactionEntity = cretePaymentTransaction(folioEntity, paymentMethod, cashCollected);

        paymentAllocationService.createPaymentAllocation(paymentTransactionEntity, selectedFolioItemEntities);

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

    private String safeClientIp(String clientIp) {
        return (clientIp == null || clientIp.isBlank()) ? "127.0.0.1" : clientIp;
    }

    private String generateTransactionCode() {
        return "PAY-" + UUID.randomUUID();
    }

    private String generateTxnRef() {
        return String.valueOf(System.currentTimeMillis());
    }

    private void handleSuccessfulPayment(PaymentTransactionEntity transaction, long vnpAmount) {
        transaction.setStatus(PaymentTransactionStatus.SUCCESS.getDbValue());

        FolioEntity folio = transaction.getFolioEntity();
        if (folio != null) {
            BigDecimal paidAmount = BigDecimal.valueOf(vnpAmount);
            BigDecimal currentTotalPaid = folio.getTotalPaid() != null ? folio.getTotalPaid() : BigDecimal.ZERO;
            folio.setTotalPaid(currentTotalPaid.add(paidAmount));

            BigDecimal totalCharges = folio.getTotalCharges() != null ? folio.getTotalCharges() : BigDecimal.ZERO;
            folio.setBalance(totalCharges.subtract(folio.getTotalPaid()));

            folioRepository.save(folio);

            if (folio.getReservationRoomEntity() != null
                    && folio.getReservationRoomEntity().getReservationEntity() != null) {

                ReservationRoomEntity room = folio.getReservationRoomEntity();
                ReservationEntity res = room.getReservationEntity();

                if (ReservationStatus.PENDING_DEPOSIT.equals(res.getStatus())) {
                    res.setStatus(ReservationStatus.CONFIRMED);
                    reservationRoomRepository.save(room);
                }
            }
        }
    }

    private void handleFailedPayment(PaymentTransactionEntity transaction) {
        transaction.setStatus(PaymentTransactionStatus.FAILED.getDbValue());

        FolioEntity folio = transaction.getFolioEntity();
        if (folio != null && folio.getReservationRoomEntity() != null) {
            ReservationEntity reservation = folio.getReservationRoomEntity().getReservationEntity();
            if (reservation != null) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                List<ReservationRoomEntity> rooms = reservationRoomRepository.findByReservationEntity(reservation);
                for (ReservationRoomEntity room : rooms) {
                    room.setStatus(ReservationRoomStatus.CANCELLED);
                }
                reservationRoomRepository.saveAll(rooms);
                reservationRepository.save(reservation);
            }
        }
    }

    private void saveVnPayDetail(PaymentTransactionEntity transaction, Map<String, String> params) {
        try {
            VnPayTransactionDetailEntity vnpayDetail = new VnPayTransactionDetailEntity();
            vnpayDetail.setPaymentTransactionEntity(transaction);
            vnpayDetail.setVnpTxnRef(params.get("vnp_TxnRef"));
            vnpayDetail.setVnpTransactionNo(params.get("vnp_TransactionNo"));
            vnpayDetail.setVnpBankCode(params.get("vnp_BankCode"));
            vnpayDetail.setVnpPayDate(params.get("vnp_PayDate"));
            vnpayDetail.setRawResponse(objectMapper.writeValueAsString(params));
            vnpayDetail.setIsActive(true);

            vnPayTransactionDetailRepository.save(vnpayDetail);
        } catch (Exception ex) {
            System.err.println("Lỗi khi lưu VnPayTransactionDetail: " + ex.getMessage());
        }
    }

    // ==========================================
    // VNPAY REFUND PROCESS (REFACTORED)
    // ==========================================

    @Override
    @Transactional
    public boolean processVnPayRefund(PaymentTransactionEntity originalTransaction, BigDecimal refundAmount, String createdBy, String clientIp) {
        VnPayTransactionDetailEntity vnpayDetail = vnPayTransactionDetailRepository
                .findByPaymentTransactionEntityId(originalTransaction.getId())
                .orElseThrow(() -> new IllegalStateException("VnPay transaction detail not found for transaction id: " + originalTransaction.getId()));

        try {
            // 1. Build Payload
            Map<String, String> payload = buildVnPayRefundPayload(originalTransaction, vnpayDetail, refundAmount, createdBy, clientIp);

            // 2. Send Request (Lấy về toàn bộ respMap)
            Map<String, Object> respMap = sendVnPayRefundRequest(payload);

            // Bóc tách respCode ra để kiểm tra
            Object respCodeObj = respMap.get("vnp_ResponseCode");
            String respCode = respCodeObj != null ? String.valueOf(respCodeObj) : null;

            // 3. Handle Response
            if ("00".equals(respCode)) {
                // Bây giờ bạn đã có respMap để truyền vào đây một cách hợp lệ
                handleSuccessfulRefund(originalTransaction, refundAmount, respMap);
                return true;
            } else {
                throw new IllegalStateException("VNPAY refund failed, response code: " + respCode);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error processing VNPAY refund: " + ex.getMessage(), ex);
        }
    }

    /**
     * Sub-method: Chuẩn bị payload và tạo chữ ký gửi lên VNPAY
     */
    private Map<String, String> buildVnPayRefundPayload(PaymentTransactionEntity originalTransaction,
                                                        VnPayTransactionDetailEntity vnpayDetail,
                                                        BigDecimal refundAmount, String createdBy, String clientIp) {
        String requestId = UUID.randomUUID().toString();
        String version = "2.1.0";
        String command = "refund";
        String tmnCode = vnPayUtil.getTmnCode();
        String txnType = refundAmount.compareTo(originalTransaction.getAmount() != null ? originalTransaction.getAmount() : BigDecimal.ZERO) == 0 ? "02" : "03";
        String txnRef = vnpayDetail.getVnpTxnRef();
        long amountVnd = refundAmount.multiply(new BigDecimal(100)).longValue();
        String amountStr = String.valueOf(amountVnd);
        String txnNo = vnpayDetail.getVnpTransactionNo();
        String txnDate = vnpayDetail.getVnpPayDate();
        String createByStr = createdBy == null ? "system" : createdBy;
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String createDate = java.time.LocalDateTime.now().format(formatter);
        String ipAddr = safeClientIp(clientIp);
        String orderInfo = "Hoan tien giao dich " + txnRef;

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("vnp_RequestId", requestId);
        payload.put("vnp_Version", version);
        payload.put("vnp_Command", command);
        payload.put("vnp_TmnCode", tmnCode);
        payload.put("vnp_TransactionType", txnType);
        payload.put("vnp_TxnRef", txnRef);
        payload.put("vnp_Amount", amountStr);
        payload.put("vnp_TransactionNo", txnNo);
        payload.put("vnp_TransactionDate", txnDate);
        payload.put("vnp_CreateBy", createByStr);
        payload.put("vnp_CreateDate", createDate);
        payload.put("vnp_IpAddr", ipAddr);
        payload.put("vnp_OrderInfo", orderInfo);

        // Băm chữ ký theo chuẩn của Refund
        String secureHash = vnPayUtil.generateRefundHash(
                requestId, version, command, tmnCode, txnType, txnRef,
                amountStr, txnNo, txnDate, createByStr, createDate, ipAddr, orderInfo
        );
        payload.put("vnp_SecureHash", secureHash);

        return payload;
    }

    /**
     * Sub-method: Gọi API của VNPAY và bóc tách mã lỗi
     */
    private Map<String, Object> sendVnPayRefundRequest(Map<String, String> payload) throws Exception {
        String url = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set(org.springframework.http.HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        String jsonBody = objectMapper.writeValueAsString(payload);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(jsonBody, headers);

        org.springframework.http.ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("VNPAY refund request failed with status: " + resp.getStatusCode());
        }

        // Trả về toàn bộ Map để bên ngoài có data lưu log
        return objectMapper.readValue(resp.getBody(), Map.class);
    }

    /**
     * Sub-method: Cập nhật lại số dư hóa đơn (Folio) và lưu log PaymentTransaction(Refund)
     */
    private void handleSuccessfulRefund(PaymentTransactionEntity originalTransaction, BigDecimal refundAmount, Map<String, Object> respMap) {
        FolioEntity folio = originalTransaction.getFolioEntity();
        if (folio != null) {
            BigDecimal currentTotalPaid = folio.getTotalPaid() != null ? folio.getTotalPaid() : BigDecimal.ZERO;
            folio.setTotalPaid(currentTotalPaid.subtract(refundAmount));

            BigDecimal totalCharges = folio.getTotalCharges() != null ? folio.getTotalCharges() : BigDecimal.ZERO;
            folio.setBalance(totalCharges.subtract(folio.getTotalPaid()));
            folioRepository.save(folio);
        }

        // 1. Tạo giao dịch chính (type = REFUND)
        PaymentTransactionEntity refundTransaction = new PaymentTransactionEntity();
        refundTransaction.setFolioEntity(folio);
        refundTransaction.setCode("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        refundTransaction.setPaymentMethod(originalTransaction.getPaymentMethod());
        refundTransaction.setAmount(refundAmount);
        refundTransaction.setType(PaymentTransactionType.REFUND.getDbValue()); // Dùng Enum nếu có, hoặc "REFUND"
        refundTransaction.setStatus(PaymentTransactionStatus.SUCCESS.getDbValue());
        refundTransaction.setTransactionReference(generateTxnRef());
        refundTransaction.setCreatedAt(Timestamp.from(Instant.now()));
        refundTransaction.setIsActive(true);

        PaymentTransactionEntity savedRefundTransaction = paymentTransactionRepository.save(refundTransaction);

        // 2. Tạo giao dịch Detail chứa log VNPAY trả về
        try {
            VnPayTransactionDetailEntity vnpayDetail = new VnPayTransactionDetailEntity();
            vnpayDetail.setPaymentTransactionEntity(savedRefundTransaction); // Nối 1-1 với giao dịch Refund vừa tạo

            // Bóc tách dữ liệu từ API Refund trả về
            vnpayDetail.setVnpTxnRef(respMap.get("vnp_TxnRef") != null ? String.valueOf(respMap.get("vnp_TxnRef")) : originalTransaction.getTransactionReference());
            vnpayDetail.setVnpTransactionNo(respMap.get("vnp_TransactionNo") != null ? String.valueOf(respMap.get("vnp_TransactionNo")) : "");
            vnpayDetail.setVnpBankCode(respMap.get("vnp_BankCode") != null ? String.valueOf(respMap.get("vnp_BankCode")) : "");
            vnpayDetail.setVnpPayDate(respMap.get("vnp_PayDate") != null ? String.valueOf(respMap.get("vnp_PayDate")) : "");
            vnpayDetail.setRawResponse(objectMapper.writeValueAsString(respMap)); // Cục log siêu quan trọng để đối soát
            vnpayDetail.setIsActive(true);

            vnPayTransactionDetailRepository.save(vnpayDetail);
        } catch (Exception ex) {
            // Chỉ log lỗi chứ không ném Exception để không làm hỏng tiến trình hoàn tiền chính
            System.err.println("Lỗi khi lưu VnPayTransactionDetail cho REFUND: " + ex.getMessage());
        }
    }
}