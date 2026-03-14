package com.product.hms.repository;

import com.product.hms.entity.RoomAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomAssetRepository extends JpaRepository<RoomAssetEntity, Long> {

    List<RoomAssetEntity> findByRoomEntityId(Long roomId);

    // Find available assets (quantity > 0)
    @Query("SELECT ra FROM RoomAssetEntity ra WHERE ra.roomEntity.id = :roomId " +
            "AND ra.quantity > 0 AND ra.isActive = true")
    List<RoomAssetEntity> findAvailableByRoomId(@Param("roomId") Long roomId);

    // Find minibar items (filter by category)
    @Query("SELECT ra FROM RoomAssetEntity ra WHERE ra.roomEntity.id = :roomId " +
            "AND ra.quantity > 0 AND ra.isActive = true " +
            "AND (LOWER(ra.assetEntity.categoryEntity.name) LIKE '%minibar%' " +
            "OR LOWER(ra.assetEntity.categoryEntity.name) LIKE '%beverage%' " +
            "OR LOWER(ra.assetEntity.categoryEntity.name) LIKE '%snack%')")
    List<RoomAssetEntity> findMinibarItemsByRoomId(@Param("roomId") Long roomId);

    // Find asset by room and asset
    @Query("SELECT ra FROM RoomAssetEntity ra WHERE ra.roomEntity.id = :roomId " +
            "AND ra.assetEntity.id = :assetId AND ra.isActive = true")
    List<RoomAssetEntity> findByRoomIdAndAssetId(
            @Param("roomId") Long roomId,
            @Param("assetId") Long assetId);
}