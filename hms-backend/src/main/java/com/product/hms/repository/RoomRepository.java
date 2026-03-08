package com.product.hms.repository;

import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    @Query("""
            SELECT r FROM RoomEntity r
            WHERE r.roomClassEntity.id = :roomClassId
              AND r.isActive = true
              AND r.id NOT IN (
                  SELECT alloc.roomEntity.id
                  FROM ReservationRoomAllocationEntity alloc
                  JOIN alloc.reservationEntity res
                  WHERE alloc.roomEntity IS NOT NULL
                    AND res.expectedCheckIn < :checkOut
                    AND res.expectedCheckOut > :checkIn
                    AND res.status IN :statuses
                    AND alloc.isActive = true
                    AND res.isActive = true
              )
            """)
    List<RoomEntity> findAvailableRoomsByClass(@Param("roomClassId") Long roomClassId,
                                               @Param("checkIn") java.sql.Timestamp checkIn,
                                               @Param("checkOut") java.sql.Timestamp checkOut,
                                               @Param("statuses") List<ReservationStatus> statuses);
}
