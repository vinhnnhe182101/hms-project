package com.product.hms.repository;

import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.enums.RoomStatus;
import com.product.hms.repository.custom.RoomRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long>, RoomRepositoryCustom {

    /**
     * Typed facade to avoid hardcoded status literals in callers.
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
     * Typed facade to get physical available rooms for manual assignment.
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
     * Typed facade to get available physical rooms by room class.
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
