package com.product.hms.repository;

import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoomEntity, Long> {

    /**
     * Tìm kiếm tất cả các ReservationRoomEntity theo ReservationEntity
     *
     * @param reservationEntity đối tượng ReservationEntity cần tìm kiếm
     * @return Danh sách các ReservationRoomEntity
     */
    List<ReservationRoomEntity> findByReservationEntity(ReservationEntity reservationEntity);

    /**
     * Xóa tất cả các ReservationRoomEntity theo ReservationEntity
     *
     * @param reservationEntity đối tượng ReservationEntity cần xóa
     */
    void deleteByReservationEntity(ReservationEntity reservationEntity);

    /**
     * Tìm kiếm tất cả các ReservationRoomEntity theo id của ReservationEntity và isActive = true
     *
     * @param reservationId id của ReservationEntity cần tìm kiếm
     * @return Danh sách các ReservationRoomEntity thỏa mãn điều kiện
     */
    List<ReservationRoomEntity> findByReservationEntity_IdAndIsActiveTrue(Long reservationId);

    boolean existsByRoomEntityAndReservationEntityAndIsActiveTrue(com.product.hms.entity.RoomEntity room, com.product.hms.entity.ReservationEntity reservation);
}
