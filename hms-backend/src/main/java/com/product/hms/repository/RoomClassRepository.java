package com.product.hms.repository;

import com.product.hms.entity.RoomAssetEntity;
import com.product.hms.entity.RoomClassEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomClassRepository extends JpaRepository<RoomClassEntity, Long> {

    // ===== LIST =====

    @Query("""
            SELECT rc.id,
                   rc.name,
                   rc.standardCapacity,
                   rc.basePrice,
                   COUNT(r.id)
            FROM RoomClassEntity rc
            LEFT JOIN RoomEntity r ON r.roomClassEntity.id = rc.id
            GROUP BY rc.id, rc.name, rc.standardCapacity, rc.basePrice
            ORDER BY rc.id ASC
            """)
    Page<Object[]> findRoomClassSummary(Pageable pageable);

    // ===== DETAIL =====

    /**
     * Lấy RoomClass kèm COUNT phòng cho trang detail.
     * Cột trả về:
     * [0] id, [1] name, [2] standardCapacity, [3] maxCapacity,
     * [4] basePrice, [5] extraPersonFee, [6] totalRooms
     */
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

    /**
     * Lấy toàn bộ RoomAsset của tất cả phòng thuộc một RoomClass.
     * JOIN FETCH load sẵn asset và category tránh N+1.
     */
    @Query("""
            SELECT ra FROM RoomAssetEntity ra
            JOIN FETCH ra.assetEntity a
            JOIN FETCH a.categoryEntity
            WHERE ra.roomEntity.roomClassEntity.id = :roomClassId
            """)
    List<RoomAssetEntity> findAssetsByRoomClassId(@Param("roomClassId") Long roomClassId);

    /**
     * Lấy các RoomClass khác (loại trừ ID hiện tại) để gợi ý.
     * Cột trả về: [0] id, [1] name, [2] standardCapacity, [3] basePrice, [4] totalRooms
     */
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
}
