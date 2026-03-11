package com.product.hms.repository;

import com.product.hms.entity.ReservationEntity;
import com.product.hms.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByStatusAndCreatedAtBefore(ReservationStatus status, Timestamp createdAt);
}

