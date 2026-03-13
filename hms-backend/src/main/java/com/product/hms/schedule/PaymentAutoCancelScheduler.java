package com.product.hms.schedule;

import com.product.hms.entity.PaymentTransactionEntity;
import com.product.hms.enums.PaymentTransactionStatus;
import com.product.hms.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Scheduler tự động chuyển trạng thái các giao dịch thanh toán PENDING sang CANCELLED
 * nếu quá thời gian chờ thanh toán.
 *
 * <p>Giúp hệ thống không giữ các giao dịch treo, đảm bảo dữ liệu nhất quán.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentAutoCancelScheduler {
    private final PaymentTransactionRepository paymentTransactionRepository;

    // Thời gian timeout (ms), đọc từ business rule BR-03: 2 giờ
    private static final long PAYMENT_TIMEOUT_MILLIS = 2 * 60 * 60 * 1000;

    /**
     * Chạy mỗi 5 phút, tìm các giao dịch PENDING quá hạn và chuyển sang CANCELLED.
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    @Transactional
    public void autoCancelPendingPayments() {
        Timestamp timeoutThreshold = Timestamp.from(Instant.now().minusMillis(PAYMENT_TIMEOUT_MILLIS));
        List<PaymentTransactionEntity> expiredPending = paymentTransactionRepository
                .findByStatusAndCreatedAtBefore(PaymentTransactionStatus.PENDING.getDbValue(), timeoutThreshold);
        if (expiredPending.isEmpty()) return;
        for (PaymentTransactionEntity tx : expiredPending) {
            tx.setStatus(PaymentTransactionStatus.CANCELLED.getDbValue());
            tx.setIsActive(false);
            log.info("[PaymentAutoCancelScheduler] Đã tự động hủy giao dịch payment id={} do quá hạn.", tx.getId());
        }
        paymentTransactionRepository.saveAll(expiredPending);
    }
}
