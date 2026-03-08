package com.product.hms.repository;

import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.ServiceBookingEntity;
import com.product.hms.enums.ServiceBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBookingEntity, Long> {
    List<ServiceBookingEntity> findByReservationRoomEntity(ReservationRoomEntity reservationRoomEntity);

    java.util.Optional<ServiceBookingEntity> findByIdAndReservationRoomEntity_Id(Long id, Long reservationRoomId);

    boolean existsByReservationRoomEntityAndStatus(ReservationRoomEntity reservationRoomEntity, ServiceBookingStatus status);
}
