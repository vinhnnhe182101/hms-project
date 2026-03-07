package com.product.hms.repository;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.ReservationRoomAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolioRepository extends JpaRepository<FolioEntity, Long> {
    Optional<FolioEntity> findByReservationRoomAllocation(ReservationRoomAllocationEntity allocation);
}
