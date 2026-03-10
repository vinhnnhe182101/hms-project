package com.product.hms.repository;

import com.product.hms.entity.RoomClassEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.product.hms.entity.RoomAssetEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomClassRepository extends JpaRepository<RoomClassEntity, Long> {

    @Query("SELECT rc.id, rc.name, rc.standardCapacity, rc.basePrice, " +
           "(SELECT COUNT(r) FROM RoomEntity r WHERE r.roomClassEntity.id = rc.id AND r.isActive = true) " +
           "FROM RoomClassEntity rc WHERE rc.isActive = true")
    Page<Object[]> findRoomClassSummaryWithoutDate(Pageable pageable);

    @Query("SELECT rc.id, rc.name, rc.standardCapacity, rc.maxCapacity, rc.basePrice, rc.extraPersonFee, " +
           "(SELECT COUNT(r) FROM RoomEntity r WHERE r.roomClassEntity.id = rc.id AND r.isActive = true) " +
           "FROM RoomClassEntity rc WHERE rc.id = :id AND rc.isActive = true")
    List<Object[]> findDetailById(@Param("id") Long id);

    @Query("SELECT rc.id, rc.name, rc.standardCapacity, rc.basePrice, " +
           "(SELECT COUNT(r) FROM RoomEntity r WHERE r.roomClassEntity.id = rc.id AND r.isActive = true) " +
           "FROM RoomClassEntity rc WHERE rc.id <> :excludeId AND rc.isActive = true")
    List<Object[]> findOtherRoomClasses(@Param("excludeId") Long excludeId);

    @Query("SELECT ra FROM RoomAssetEntity ra WHERE ra.roomEntity.roomClassEntity.id = :roomClassId AND ra.isActive = true")
    List<RoomAssetEntity> findAssetsByRoomClassId(@Param("roomClassId") Long roomClassId);
}

