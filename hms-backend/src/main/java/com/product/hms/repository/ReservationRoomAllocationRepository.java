package com.product.hms.repository;

import com.product.hms.entity.ReservationRoomAllocationEntity;
import com.product.hms.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRoomAllocationRepository extends JpaRepository<ReservationRoomAllocationEntity, Long> {

    @Query("SELECT rra FROM ReservationRoomAllocationEntity rra " +
           "WHERE rra.reservationEntity.customerEntity.id = :customerId " +
           "AND rra.reservationEntity.status IN :statuses " +
           "AND rra.isActive = true")
    List<ReservationRoomAllocationEntity> findActiveAllocationsByCustomer(
            @Param("customerId") Long customerId,
            @Param("statuses") List<ReservationStatus> statuses);
}
