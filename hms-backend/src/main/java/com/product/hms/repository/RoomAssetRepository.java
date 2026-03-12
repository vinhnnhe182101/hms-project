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
    
    @Query("SELECT ra FROM RoomAssetEntity ra WHERE ra.roomEntity.id = :roomId AND ra.quantity > 0")
    List<RoomAssetEntity> findAvailableByRoomId(@Param("roomId") Long roomId);
    
    @Query("SELECT ra FROM RoomAssetEntity ra WHERE ra.roomEntity.id = :roomId " +
           "AND ra.assetEntity.categoryEntity.name IN ('Minibar', 'Beverage', 'Snack')")
    List<RoomAssetEntity> findMinibarItemsByRoomId(@Param("roomId") Long roomId);
}