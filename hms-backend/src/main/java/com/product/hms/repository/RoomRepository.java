package com.product.hms.repository;

import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Map;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    /**
     * Typed facade to avoid hardcoded status literals in callers.
     */
    default Map<Long, Integer> countAvailableRoomsByRoomClass(Timestamp checkInDate, Timestamp checkOutDate) {
        return countAvailableRoomsByRoomClass(
                checkInDate,
                checkOutDate,
                RoomStatus.AVAILABLE.name(),
                ReservationStatus.CONFIRMED.name(),
                ReservationStatus.IN_HOUSE.name()
        );
    }

    /**
     * Get available room count by room class.
     */
    @Query(value = """
            SELECT
                rc.id AS roomClassId,
                COUNT(DISTINCT r.id) AS availableRooms
            FROM room r
            INNER JOIN room_class rc ON r.room_class_id = rc.id
            WHERE r.is_active = true
              AND r.status = :roomAvailableStatus
              AND rc.is_active = true
              AND r.id NOT IN (
                  SELECT DISTINCT rra.room_id
                  FROM reservation_room_allocation rra
                  INNER JOIN reservation res ON rra.reservation_id = res.id
                  WHERE rra.room_id IS NOT NULL
                    AND res.is_active = true
                    AND rra.is_active = true
                    AND res.status IN (:reservationConfirmedStatus, :reservationInHouseStatus)
                    AND NOT (
                        res.expected_check_out <= :checkInDate
                        OR res.expected_check_in >= :checkOutDate
                    )
              )
            GROUP BY rc.id
            """, nativeQuery = true)
    Map<Long, Integer> countAvailableRoomsByRoomClass(
            @Param("checkInDate") Timestamp checkInDate,
            @Param("checkOutDate") Timestamp checkOutDate,
            @Param("roomAvailableStatus") String roomAvailableStatus,
            @Param("reservationConfirmedStatus") String reservationConfirmedStatus,
            @Param("reservationInHouseStatus") String reservationInHouseStatus
    );
}
