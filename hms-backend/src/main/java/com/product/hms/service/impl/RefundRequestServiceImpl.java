package com.product.hms.service.impl;

import com.product.hms.dto.request.RejectRefundRequest;
import com.product.hms.dto.response.PaymentTransactionResponse;
import com.product.hms.dto.response.RefundRequestResponse;
import com.product.hms.entity.PaymentTransactionEntity;
import com.product.hms.entity.RefundRequestEntity;
import com.product.hms.entity.StaffEntity;
import com.product.hms.enums.PaymentMethod;
import com.product.hms.enums.RefundRequestStatus;
import com.product.hms.repository.RefundRequestRepository;
import com.product.hms.repository.StaffRepository;
import com.product.hms.service.PaymentService;
import com.product.hms.service.RefundRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefundRequestServiceImpl implements RefundRequestService {

    private final RefundRequestRepository refundRequestRepository;
    private final StaffRepository staffRepository;
    // Đã xóa PaymentTransactionRepository vì có thể lấy trực tiếp từ Relation
    private final PaymentService paymentService;

    @Override
    @Transactional(readOnly = true) // Ngăn lỗi LazyInitializationException
    public Page<RefundRequestResponse> getPendingRefundRequests(Pageable pageable) {
        // Sử dụng Enum thay cho String hardcode "PENDING"
        Page<RefundRequestEntity> page = refundRequestRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("status"), RefundRequestStatus.PENDING.name()), pageable);
        return page.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true) // Ngăn lỗi LazyInitializationException
    public RefundRequestResponse getRefundRequestById(Long id) {
        RefundRequestEntity e = refundRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Refund request not found: " + id));
        return toDto(e);
    }

    @Override
    @Transactional
    public RefundRequestResponse rejectRefundRequest(Long id, Long adminStaffId, RejectRefundRequest request) {
        RefundRequestEntity e = refundRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Refund request not found: " + id));

        if (!RefundRequestStatus.PENDING.name().equals(e.getStatus())) {
            throw new IllegalStateException("Refund request is not pending");
        }

        StaffEntity admin = staffRepository.findById(adminStaffId)
                .orElseThrow(() -> new IllegalArgumentException("Admin staff not found: " + adminStaffId));

        // Set enum REJECTED
        e.setStatus(RefundRequestStatus.REJECTED.name());
        e.setRejectReason(request.getRejectReason());
        e.setApprovedByEntity(admin);
        e.setUpdatedAt(Timestamp.from(Instant.now())); // Cập nhật lại thời gian Update

        refundRequestRepository.save(e);
        return toDto(e);
    }

    @Override
    @Transactional
    public RefundRequestResponse approveRefundRequest(Long id, Long adminStaffId, String clientIp) {
        RefundRequestEntity e = refundRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Refund request not found: " + id));

        if (!RefundRequestStatus.PENDING.name().equals(e.getStatus())) {
            throw new IllegalStateException("Refund request is not pending");
        }

        StaffEntity admin = staffRepository.findById(adminStaffId)
                .orElseThrow(() -> new IllegalArgumentException("Admin staff not found: " + adminStaffId));

        // Lấy Transaction trực tiếp qua relation (không cần query lại DB tốn hiệu năng)
        PaymentTransactionEntity original = e.getPaymentTransactionEntity();

        if (original != null && PaymentMethod.VNPAY.getDbValue().equalsIgnoreCase(original.getPaymentMethod())) {
            boolean ok = paymentService.processVnPayRefund(original, e.getAmount(), admin.getFullName(), clientIp);
            if (!ok) {
                // Nếu hoàn tiền qua VNPAY gặp sự cố (ví dụ lỗi mạng), chuyển trạng thái thành FAILED
                e.setStatus(RefundRequestStatus.FAILED.name());
                e.setUpdatedAt(Timestamp.from(Instant.now()));
                refundRequestRepository.save(e);
                throw new IllegalStateException("VNPAY refund failed");
            }
        }

        // Nếu mọi thứ ok (hoặc là thanh toán CASH) -> Set APPROVED
        e.setStatus(RefundRequestStatus.APPROVED.name());
        e.setApprovedByEntity(admin);
        e.setUpdatedAt(Timestamp.from(Instant.now()));

        refundRequestRepository.save(e);
        return toDto(e);
    }

    private RefundRequestResponse toDto(RefundRequestEntity e) {
        String requestedBy = e.getRequestedByEntity() != null ? e.getRequestedByEntity().getFullName() : null;
        String approvedBy = e.getApprovedByEntity() != null ? e.getApprovedByEntity().getFullName() : null;

        PaymentTransactionEntity pt = e.getPaymentTransactionEntity();
        PaymentTransactionResponse ptResp = null;

        if (pt != null) {
            ptResp = new PaymentTransactionResponse(
                    pt.getId(),
                    pt.getFolioEntity() != null ? pt.getFolioEntity().getId() : null,
                    pt.getCode(),
                    pt.getTransactionReference(),
                    pt.getPaymentMethod(),
                    pt.getAmount(),
                    pt.getType(),
                    pt.getStatus(),
                    pt.getCreatedAt()
            );
        }

        return new RefundRequestResponse(
                e.getId(), e.getAmount(), e.getReason(), e.getRejectReason(),
                e.getStatus(), requestedBy, approvedBy, e.getCreatedAt(), e.getUpdatedAt(), ptResp
        );
    }
}