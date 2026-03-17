package com.product.hms.repository;

import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.enums.ReservationRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT rr FROM ReservationRoomEntity rr " +
           "JOIN rr.reservationEntity res " +
           "WHERE res.customerEntity.id = :customerId " +
           "AND rr.isActive = true " +
           "AND res.status IN :statuses")
    List<ReservationRoomEntity> findActiveAllocationsByCustomer(
            @org.springframework.data.repository.query.Param("customerId") Long customerId,
            @org.springframework.data.repository.query.Param("statuses") List<com.product.hms.enums.ReservationStatus> statuses);

    /**
     * Tìm kiếm tất cả các ReservationRoomEntity theo id của ReservationEntity và isActive = true
     */
    List<ReservationRoomEntity> findByReservationEntity_IdAndIsActiveTrue(Long reservationId);

    /**
     * Kiểm tra sự tồn tại của một ReservationRoomEntity theo RoomEntity, ReservationEntity và isActive = true
     *
     * @param room đối tượng RoomEntity cần kiểm tra
     * @param reservation đối tượng ReservationEntity cần kiểm tra
     */
    boolean existsByRoomEntityAndReservationEntityAndIsActiveTrue(com.product.hms.entity.RoomEntity room, com.product.hms.entity.ReservationEntity reservation);
    // Find by reservation

    // Find by room
    List<ReservationRoomEntity> findByRoomEntityId(Long roomId);

    // Find current active reservation for a room
    @Query("SELECT rr FROM ReservationRoomEntity rr WHERE rr.roomEntity.id = :roomId " +
            "AND rr.status IN :statuses AND rr.isActive = true")
    Optional<ReservationRoomEntity> findByRoomIdAndStatusIn(
            @Param("roomId") Long roomId,
            @Param("statuses") List<ReservationRoomStatus> statuses);
    default Optional<ReservationRoomEntity> findCurrentReservationByRoomId(Long roomId) {
        return findByRoomIdAndStatusIn(roomId,
                List.of(ReservationRoomStatus.CHECKED_IN, ReservationRoomStatus.ASSIGNED));
    }
    // Find by status
    List<ReservationRoomEntity> findByStatus(ReservationRoomStatus status);


    List<ReservationRoomEntity> findByReservationEntityId(Long reservationId);

    @Query("SELECT rr FROM ReservationRoomEntity rr WHERE rr.reservationEntity.id = :reservationId")
    List<ReservationRoomEntity> findByReservationId(@Param("reservationId") Long reservationId);
}
