package com.product.hms.repository;

import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRoomAllocationRepository extends JpaRepository<ReservationRoomEntity, Long> {

    /**
     * Find all allocations for a reservation
     */
    List<ReservationRoomEntity> findByReservationEntity(ReservationEntity reservationEntity);

    /**
     * Delete all allocations for a reservation.
     */
    void deleteByReservationEntity(ReservationEntity reservationEntity);
}
