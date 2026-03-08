package com.product.hms.repository;

import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomOccupantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomOccupantRepository extends JpaRepository<RoomOccupantEntity, Long> {
    List<RoomOccupantEntity> findByReservationRoomEntityAndIsActiveTrue(ReservationRoomEntity reservationRoom);
}

