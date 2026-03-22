package com.product.hms.repository;

import com.product.hms.entity.RoomAssetEntity;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import com.product.hms.enums.ReservationStatus;

@Repository
public interface RoomClassRepository extends JpaRepository<RoomClassEntity, Long> {

        Optional<RoomClassEntity> findByIdAndIsActiveTrue(Long id);

        Optional<RoomClassEntity> findByNameIgnoreCaseAndIsActiveTrue(String name);

        Page<RoomClassEntity> findAllByIsActiveTrue(Pageable pageable);

        boolean existsByNameIgnoreCaseAndIsActiveTrue(String name);

        boolean existsByNameIgnoreCaseAndIsActiveTrueAndIdNot(String name, Long id);

    @Query("""
            SELECT roomClass.id,
                   roomClass.name,
                   roomClass.standardCapacity,
                   roomClass.maxCapacity,
                   roomClass.basePrice,
                   roomClass.extraPersonFee,
                   (COUNT(DISTINCT r.id) - 
                    (SELECT COUNT(DISTINCT r2.id) 
                     FROM ReservationRoomEntity alloc
                     JOIN alloc.reservationEntity res
                     JOIN alloc.roomEntity r2
                     WHERE r2.roomClassEntity.id = roomClass.id
                       AND res.expectedCheckIn < :checkOut
                       AND res.expectedCheckOut > :checkIn
                       AND res.status IN :statuses
                       AND alloc.isActive = true
                       AND res.isActive = true
                       AND r2.isActive = true))
            FROM RoomClassEntity roomClass
            LEFT JOIN RoomEntity r ON r.roomClassEntity.id = roomClass.id AND r.isActive = true
            WHERE roomClass.isActive = true
            GROUP BY roomClass.id, roomClass.name, roomClass.standardCapacity, roomClass.maxCapacity, roomClass.basePrice, roomClass.extraPersonFee
            """)
    Page<Object[]> findRoomClassSummary(@Param("checkIn") Timestamp checkIn,
                                        @Param("checkOut") Timestamp checkOut,
                                        @Param("statuses") List<ReservationStatus> statuses,
                                        Pageable pageable);

    @Query("""
            SELECT roomClass.id,
                   roomClass.name,
                   roomClass.standardCapacity,
                   roomClass.maxCapacity,
                   roomClass.basePrice,
                   roomClass.extraPersonFee,
                   COUNT(r.id)
            FROM RoomClassEntity roomClass
            LEFT JOIN RoomEntity r ON r.roomClassEntity.id = roomClass.id AND r.isActive = true
            WHERE roomClass.isActive = true
            GROUP BY roomClass.id, roomClass.name, roomClass.standardCapacity, roomClass.maxCapacity, roomClass.basePrice, roomClass.extraPersonFee
            """)
    Page<Object[]> findRoomClassSummaryWithoutDate(Pageable pageable);

    @Query("""
            SELECT roomClass.id,
                   roomClass.name,
                   roomClass.standardCapacity,
                   roomClass.maxCapacity,
                   roomClass.basePrice,
                   roomClass.extraPersonFee,
                   COUNT(r.id)
            FROM RoomClassEntity roomClass
            LEFT JOIN RoomEntity r ON r.roomClassEntity.id = roomClass.id
            WHERE roomClass.id = :id
            GROUP BY roomClass.id, roomClass.name, roomClass.standardCapacity, roomClass.maxCapacity,
                     roomClass.basePrice, roomClass.extraPersonFee
            """)
    List<Object[]> findDetailById(@Param("id") Long id);

    @Query("""
            SELECT roomClass.id,
                   roomClass.name,
                   roomClass.standardCapacity,
                   roomClass.maxCapacity,
                   roomClass.basePrice,
                   roomClass.extraPersonFee,
                   COUNT(r.id)
            FROM RoomClassEntity roomClass
            LEFT JOIN RoomEntity r ON r.roomClassEntity.id = roomClass.id
            WHERE roomClass.id <> :excludeId
            GROUP BY roomClass.id, roomClass.name, roomClass.standardCapacity, roomClass.maxCapacity, roomClass.basePrice, roomClass.extraPersonFee
            ORDER BY roomClass.id ASC
            """)
    List<Object[]> findOtherRoomClasses(@Param("excludeId") Long excludeId);

    @Query("SELECT ra FROM RoomAssetEntity ra WHERE ra.roomEntity.roomClassEntity.id = :roomClassId AND ra.isActive = true")
    List<RoomAssetEntity> findAssetsByRoomClassId(@Param("roomClassId") Long roomClassId);
}

