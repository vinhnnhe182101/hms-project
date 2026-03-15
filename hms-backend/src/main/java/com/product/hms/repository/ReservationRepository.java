package com.product.hms.repository;

import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>, JpaSpecificationExecutor<ReservationEntity> {
    List<ReservationEntity> findByStatusAndCreatedAtBefore(ReservationStatus status, Timestamp createdAt);

    List<ReservationEntity> findByCustomerEntity(CustomerEntity customerEntity);

    long countByStatusAndIsActiveTrue(ReservationStatus status);

    List<ReservationEntity> findTop5ByIsActiveTrueOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(r) FROM ReservationEntity r WHERE r.isActive = true AND r.expectedCheckIn >= :start AND r.expectedCheckIn <= :end")
    long countCheckInsToday(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(r) FROM ReservationEntity r WHERE r.isActive = true AND r.expectedCheckOut >= :start AND r.expectedCheckOut <= :end")
    long countCheckOutsToday(@org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);
}
