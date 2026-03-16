package com.product.hms.repository;

import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>, JpaSpecificationExecutor<ReservationEntity> {
    List<ReservationEntity> findByStatusAndCreatedAtBefore(ReservationStatus status, Timestamp createdAt);

    List<ReservationEntity> findByCustomerEntity(CustomerEntity customerEntity);

    List<ReservationEntity> findByCustomerEntityIdOrderByCreatedAtDesc(Long customerId);

    Optional<ReservationEntity> findByIdAndCustomerEntityId(Long id, Long customerId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.customerEntity.id = :customerId " +
            "AND r.status IN :statuses ORDER BY r.expectedCheckIn ASC")
    List<ReservationEntity> findByCustomerEntityIdAndStatusIn(
            @Param("customerId") Long customerId,
            @Param("statuses") List<ReservationStatus> statuses);
}

