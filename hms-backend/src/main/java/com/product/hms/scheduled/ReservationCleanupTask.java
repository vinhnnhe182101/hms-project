package com.product.hms.scheduled;

import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.enums.FolioStatus;
import com.product.hms.enums.ReservationRoomStatus;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.repository.FolioRepository;
import com.product.hms.repository.ReservationRepository;
import com.product.hms.repository.ReservationRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationCleanupTask {

    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final FolioRepository folioRepository;

    // Chạy mỗi phút (60000ms) kiểm tra một lần
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredReservations() {
        // Quá hạn 15 phút (so với currentTime hiện tại là Ho Chi Minh)
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDateTime expireTime = LocalDateTime.now(zoneId).minusMinutes(15);
        Timestamp expireTimestamp = Timestamp.valueOf(expireTime);

        List<ReservationEntity> expiredReservations = reservationRepository
                .findByStatusAndCreatedAtBefore(ReservationStatus.PENDING_DEPOSIT, expireTimestamp);

        if (!expiredReservations.isEmpty()) {
            log.info("Found {} expired reservations waiting for deposit. Cancelling them...", expiredReservations.size());

            for (ReservationEntity reservation : expiredReservations) {
                // Đổi trạng thái Reservation sang CANCELLED
                reservation.setStatus(ReservationStatus.CANCELLED);
                
                // Mở khoá toàn bộ ReservationRoom thành CANCELED để trả phòng
                List<ReservationRoomEntity> reservationRooms = reservationRoomRepository
                        .findByReservationEntity_IdAndIsActiveTrue(reservation.getId());
                
                for (ReservationRoomEntity room : reservationRooms) {
                    room.setStatus(ReservationRoomStatus.CANCELLED);
                    
                    // Nều có tạo Folio cho những phòng này thì cũng đóng nốt (nếu trước đó đang mở)
                    folioRepository.findByReservationRoomEntity(room).ifPresent(folio -> {
                        folio.setStatus(FolioStatus.CLOSED);
                        folioRepository.save(folio);
                    });
                }
                
                reservationRoomRepository.saveAll(reservationRooms);
                reservationRepository.save(reservation);

                log.info("Successfully cancelled expired Reservation code: {}", reservation.getCode());
            }
        }
    }
}
