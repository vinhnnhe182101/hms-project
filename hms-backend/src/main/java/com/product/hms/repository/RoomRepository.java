package com.product.hms.repository;

import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.RoomStatus;
import com.product.hms.repository.custom.RoomRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long>, RoomRepositoryCustom {
    List<RoomEntity> findByRoomClassEntity_IdAndStatusAndIsActiveTrueOrderByIdAsc(
            Long roomClassId,
            RoomStatus status
    );
}
