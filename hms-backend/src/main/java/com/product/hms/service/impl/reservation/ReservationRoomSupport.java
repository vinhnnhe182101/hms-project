package com.product.hms.service.impl.reservation;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.ReservationRoomStatus;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.enums.RoomStatus;
import com.product.hms.repository.ReservationRoomRepository;
import com.product.hms.repository.RoomRepository;

import java.math.BigDecimal;
import java.util.List;

public class ReservationRoomSupport {
    /**
     * Lấy FolioEntity theo ReservationRoomEntity. Nếu không tìm thấy sẽ throw BusinessException.
     *
     * @param folioRepository       Repository folio
     * @param reservationRoomEntity ReservationRoom cần lấy folio
     * @return FolioEntity tương ứng
     */
    public static FolioEntity getFolioByReservationRoom(com.product.hms.repository.FolioRepository folioRepository, ReservationRoomEntity reservationRoomEntity) {
        return folioRepository.findByReservationRoomEntity(reservationRoomEntity)
                .orElseThrow(() -> new com.product.hms.exception.BusinessException(
                        com.product.hms.exception.ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservation room ID: " + reservationRoomEntity.getId()
                ));
    }

    /**
     * Lấy danh sách RoomOccupantEntity đang active theo ReservationRoomEntity.
     *
     * @param roomOccupantRepository Repository occupant
     * @param reservationRoomEntity  ReservationRoom cần lấy occupant
     * @return Danh sách RoomOccupantEntity active
     */
    public static List<com.product.hms.entity.RoomOccupantEntity> getActiveRoomOccupantsByReservationRoom(
            com.product.hms.repository.RoomOccupantRepository roomOccupantRepository,
            ReservationRoomEntity reservationRoomEntity
    ) {
        return roomOccupantRepository.findByReservationRoomEntityAndIsActiveTrue(reservationRoomEntity);
    }

    /**
     * Lấy danh sách FolioItemEntity đang active theo FolioEntity.
     *
     * @param folioItemRepository Repository folio item
     * @param folioEntity         Folio cần lấy item
     * @return Danh sách FolioItemEntity active
     */
    public static List<com.product.hms.entity.FolioItemEntity> getActiveFolioItemsByFolio(
            com.product.hms.repository.FolioItemRepository folioItemRepository,
            FolioEntity folioEntity
    ) {
        return folioItemRepository.findByFolioEntityAndIsActiveTrue(folioEntity);
    }

    /**
     * Tính phí đổi phòng dựa trên giá tại thời điểm đặt phòng (priceAtBooking) của phòng cũ và giá hiện tại của phòng mới.
     * Nếu phòng cũ không có priceAtBooking thì lấy basePrice của hạng phòng cũ.
     *
     * @param newRoom         Phòng mới muốn chuyển sang
     * @param reservationRoom Phòng cũ đang ở
     * @param reservation     Đặt phòng hiện tại
     * @return Số tiền chênh lệch (âm nếu hạ hạng, dương nếu nâng hạng, 0 nếu cùng hạng)
     */
    public static BigDecimal calculateRoomChangeFeeWithBookingPrice(RoomEntity newRoom, ReservationRoomEntity reservationRoom, ReservationEntity reservation) {
        BigDecimal oldPrice = reservationRoom.getPriceAtBooking() != null
                ? reservationRoom.getPriceAtBooking()
                : reservationRoom.getRoomClassEntity().getBasePrice();
        BigDecimal newPrice = newRoom.getRoomClassEntity().getBasePrice();
        BigDecimal priceDiff = newPrice.subtract(oldPrice);
        long days = java.time.temporal.ChronoUnit.DAYS.between(java.time.Instant.now(), reservation.getExpectedCheckOut().toInstant());
        if (days < 1) days = 1;
        return priceDiff.multiply(BigDecimal.valueOf(days));
    }

    /**
     * Cập nhật trạng thái phòng khi đổi phòng:
     * - Phòng cũ về AVAILABLE
     * - Phòng mới sang OCCUPIED
     * - Không cập nhật lại priceAtBooking, giữ nguyên giá gốc để tính tiền phòng cuối cùng
     *
     * @param reservationRoom           ReservationRoom cần đổi
     * @param newRoom                   Phòng mới
     * @param roomRepository            Repository phòng
     * @param reservationRoomRepository Repository reservationRoom
     */
    public static void updateRoomStatusAndBookingPrice(ReservationRoomEntity reservationRoom, RoomEntity newRoom,
                                                       RoomRepository roomRepository, ReservationRoomRepository reservationRoomRepository) {
        RoomEntity oldRoom = reservationRoom.getRoomEntity();
        if (oldRoom != null) {
            oldRoom.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(oldRoom);
        }
        reservationRoom.setRoomEntity(newRoom);
        reservationRoomRepository.save(reservationRoom);
        newRoom.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(newRoom);
    }

    /**
     * Cập nhật ghi chú cho reservationRoom nếu có note mới từ request.
     *
     * @param note                      Ghi chú mới
     * @param reservationRoom           ReservationRoom cần cập nhật
     * @param reservationRoomRepository Repository reservationRoom
     */
    public static void updateNoteIfNeeded(String note, ReservationRoomEntity reservationRoom, ReservationRoomRepository reservationRoomRepository) {
        if (note != null && !note.isBlank()) {
            reservationRoomRepository.save(reservationRoom);
        }
    }

    /**
     * Kiểm tra nếu tất cả các phòng trong reservation đã thanh toán đủ (folio balance <= 0) thì cập nhật trạng thái reservation thành FINISHED.
     *
     * @param reservation               Đặt phòng cần kiểm tra
     * @param reservationRoomRepository Repository phòng
     * @param folioRepository           Repository folio
     * @param reservationRepository     Repository reservation
     */
    public static void updateReservationStatusIfAllPaid(ReservationEntity reservation,
                                                        ReservationRoomRepository reservationRoomRepository,
                                                        com.product.hms.repository.FolioRepository folioRepository,
                                                        com.product.hms.repository.ReservationRepository reservationRepository) {
        if (reservation.getStatus() != ReservationStatus.CHECKED_OUT) {
            return;
        }
        List<ReservationRoomEntity> allRooms = reservationRoomRepository
                .findByReservationEntity_IdAndIsActiveTrue(reservation.getId());
        boolean allPaid = allRooms.stream().allMatch(room -> {
            FolioEntity folio = folioRepository.findByReservationRoomEntity(room).orElse(null);
            return folio != null && folio.getBalance().signum() <= 0;
        });
        if (allPaid) {
            reservation.setStatus(ReservationStatus.FINISHED);
            reservationRepository.save(reservation);
        }
    }

    /**
     * Kiểm tra nếu tất cả các phòng trong reservation đã CHECKED_OUT thì cập nhật trạng thái reservation thành CHECKED_OUT.
     *
     * @param reservation               Đặt phòng cần kiểm tra
     * @param reservationRoomRepository Repository phòng
     * @param reservationRepository     Repository reservation
     */
    public static void updateReservationStatusIfAllCheckedOut(ReservationEntity reservation,
                                                              ReservationRoomRepository reservationRoomRepository,
                                                              com.product.hms.repository.ReservationRepository reservationRepository) {
        List<ReservationRoomEntity> allRooms = reservationRoomRepository
                .findByReservationEntity_IdAndIsActiveTrue(reservation.getId());
        boolean allCheckedOut = allRooms.stream()
                .allMatch(r -> r.getStatus() == ReservationRoomStatus.CHECKED_OUT);
        if (allCheckedOut) {
            reservation.setStatus(ReservationStatus.CHECKED_OUT);
            reservationRepository.save(reservation);
        }
    }
}
