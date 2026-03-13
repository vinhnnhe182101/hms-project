package com.product.hms.repository;

import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.ServiceBookingEntity;
import com.product.hms.enums.ServiceBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBookingEntity, Long> {
    /**
     * Tìm kiếm danh sách ServiceBookingEntity theo ReservationRoomEntity.
     *
     * @param reservationRoomEntity đối tượng ReservationRoomEntity cần tìm kiếm
     * @return Danh sách ServiceBookingEntity thỏa mãn điều kiện
     */
    List<ServiceBookingEntity> findByReservationRoomEntity(ReservationRoomEntity reservationRoomEntity);

    /**
     * Tìm kiếm ServiceBookingEntity theo id và ReservationRoomEntity.
     *
     * @param id                id của ServiceBookingEntity cần tìm kiếm
     * @param reservationRoomId id của ReservationRoomEntity cần tìm kiếm
     * @return Optional chứa ServiceBookingEntity nếu tìm thấy, hoặc Optional.empty() nếu không tìm thấy
     */
    Optional<ServiceBookingEntity> findByIdAndReservationRoomEntity_Id(Long id, Long reservationRoomId);

    /**
     * Kiểm tra sự tồn tại của ServiceBookingEntity theo ReservationRoomEntity và ServiceBookingStatus.
     *
     * @param reservationRoomEntity đối tượng ReservationRoomEntity cần tìm kiếm
     * @param serviceBookingStatus  trạng thái ServiceBookingStatus cần tìm kiếm
     * @return true nếu tồn tại ít nhất một ServiceBookingEntity thỏa mãn điều kiện, ngược lại trả về false
     */
    boolean existsByReservationRoomEntityAndStatus(ReservationRoomEntity reservationRoomEntity, ServiceBookingStatus serviceBookingStatus);
}
