package com.product.hms.repository;

import com.product.hms.entity.RoomClassEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.product.hms.entity.RoomAssetEntity;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import com.product.hms.enums.ReservationStatus;

@Repository
public interface RoomClassRepository extends JpaRepository<RoomClassEntity, Long> {

    @Query("""
            SELECT rc.id,
                   rc.name,
                   rc.standardCapacity,
                   rc.basePrice,
                   (COUNT(DISTINCT r.id) - 
                    (SELECT COUNT(DISTINCT r2.id) 
                     FROM ReservationRoomEntity alloc
                     JOIN alloc.reservationEntity res
                     JOIN alloc.roomEntity r2
                     WHERE r2.roomClassEntity.id = rc.id
                       AND res.expectedCheckIn < :checkOut
                       AND res.expectedCheckOut > :checkIn
                       AND res.status IN :statuses
                       AND alloc.isActive = true
                       AND res.isActive = true
                       AND r2.isActive = true))
            FROM RoomClassEntity rc
            LEFT JOIN RoomEntity r ON r.roomClassEntity.id = rc.id AND r.isActive = true
            WHERE rc.isActive = true
            GROUP BY rc.id, rc.name, rc.standardCapacity, rc.basePrice
            ORDER BY rc.id ASC
            """)
    Page<Object[]> findRoomClassSummary(@Param("checkIn") Timestamp checkIn,
                                        @Param("checkOut") Timestamp checkOut,
                                        @Param("statuses") List<ReservationStatus> statuses,
                                        Pageable pageable);

    @Query("""
            SELECT rc.id,
                   rc.name,
                   rc.standardCapacity,
                   rc.basePrice,
                   COUNT(r.id)
            FROM RoomClassEntity rc
            LEFT JOIN RoomEntity r ON r.roomClassEntity.id = rc.id AND r.isActive = true
            WHERE rc.isActive = true
            GROUP BY rc.id, rc.name, rc.standardCapacity, rc.basePrice
            ORDER BY rc.id ASC
            """)
    Page<Object[]> findRoomClassSummaryWithoutDate(Pageable pageable);

    @Query("""
            SELECT rc.id,
                   rc.name,
                   rc.standardCapacity,
                   rc.maxCapacity,
                   rc.basePrice,
                   rc.extraPersonFee,
                   COUNT(r.id)
            FROM RoomClassEntity rc
            LEFT JOIN RoomEntity r ON r.roomClassEntity.id = rc.id
            WHERE rc.id = :id
            GROUP BY rc.id, rc.name, rc.standardCapacity, rc.maxCapacity,
                     rc.basePrice, rc.extraPersonFee
            """)
    List<Object[]> findDetailById(@Param("id") Long id);

    @Query("""
            SELECT rc.id,
                   rc.name,
                   rc.standardCapacity,
                   rc.basePrice,
                   COUNT(r.id)
            FROM RoomClassEntity rc
            LEFT JOIN RoomEntity r ON r.roomClassEntity.id = rc.id
            WHERE rc.id <> :excludeId
            GROUP BY rc.id, rc.name, rc.standardCapacity, rc.basePrice
            ORDER BY rc.id ASC
            """)
    List<Object[]> findOtherRoomClasses(@Param("excludeId") Long excludeId);

    @Query("SELECT ra FROM RoomAssetEntity ra WHERE ra.roomEntity.roomClassEntity.id = :roomClassId AND ra.isActive = true")
    List<RoomAssetEntity> findAssetsByRoomClassId(@Param("roomClassId") Long roomClassId);
}

