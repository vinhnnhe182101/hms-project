package com.product.hms.repository;

import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.enums.RoomStatus;
import com.product.hms.repository.custom.RoomRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long>, RoomRepositoryCustom {

    @Query("""
            SELECT r FROM RoomEntity r
            WHERE r.roomClassEntity.id = :roomClassId
              AND r.isActive = true
              AND r.id NOT IN (
                  SELECT alloc.roomEntity.id
                  FROM ReservationRoomEntity alloc
                  JOIN alloc.reservationEntity res
                  WHERE alloc.roomEntity IS NOT NULL
                    AND res.expectedCheckIn < :checkOut
                    AND res.expectedCheckOut > :checkIn
                    AND res.status IN :statuses
                    AND alloc.isActive = true
                    AND res.isActive = true
                    AND r.isActive = true
              )
            """)
    List<RoomEntity> findAvailableRoomsByClass(@Param("roomClassId") Long roomClassId,
                                               @Param("checkIn")Timestamp checkIn,
                                               @Param("checkOut") Timestamp checkOut,
                                               @Param("statuses") List<ReservationStatus> statuses);

    /**
     * Đếm số lượng phòng còn trống theo từng loại phòng (room class) trong khoảng thời gian chỉ định.
     *
     * @param checkInDate  ngày nhận phòng
     * @param checkOutDate ngày trả phòng
     * @return Map(roomClassId, số lượng phòng còn trống)
     */
    default Map<Long, Integer> countAvailableRoomsByRoomClass(Timestamp checkInDate, Timestamp checkOutDate) {
        return countAvailableRoomsByRoomClass(
                checkInDate,
                checkOutDate,
                RoomStatus.AVAILABLE,
                ReservationStatus.CONFIRMED,
                ReservationStatus.IN_HOUSE
        );
    }

    /**
     * Lấy danh sách phòng còn trống trong khoảng thời gian chỉ định.
     *
     * @param checkInDate  ngày nhận phòng
     * @param checkOutDate ngày trả phòng
     * @return List<RoomEntity> danh sách phòng còn trống
     */
    default List<RoomEntity> findAvailableRoomsForPeriod(Timestamp checkInDate, Timestamp checkOutDate) {
        return findAvailableRoomsForPeriod(
                checkInDate,
                checkOutDate,
                RoomStatus.AVAILABLE,
                ReservationStatus.CONFIRMED,
                ReservationStatus.IN_HOUSE
        );
    }

    /**
     * Lấy danh sách phòng còn trống theo loại phòng (roomClassId) trong khoảng thời gian chỉ định.
     *
     * @param checkInDate  ngày nhận phòng
     * @param checkOutDate ngày trả phòng
     * @param roomClassId  id loại phòng
     * @return List<RoomEntity> Danh sách phòng còn trống theo loại phòng
     */
    default List<RoomEntity> findAvailableRoomsForPeriodByRoomClassId(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            Long roomClassId
    ) {
        return findAvailableRoomsForPeriodByRoomClassId(
                checkInDate,
                checkOutDate,
                roomClassId,
                RoomStatus.AVAILABLE,
                ReservationStatus.CONFIRMED,
                ReservationStatus.IN_HOUSE
        );
    }


    List<RoomEntity> findByRoomClassEntity_IdAndStatusAndIsActiveTrueOrderByIdAsc(
            Long roomClassId,
            RoomStatus status
    );

    Optional<RoomEntity> findFirstByRoomClassEntity_IdAndStatusAndIsActiveTrueOrderByIdAsc(
            Long roomClassId,
            RoomStatus status
    );

    /**
     * Fetch all active rooms for floor matrix display
     */
    List<RoomEntity> findByIsActiveTrue();
}
