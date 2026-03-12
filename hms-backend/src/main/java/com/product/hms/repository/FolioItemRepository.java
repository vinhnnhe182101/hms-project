package com.product.hms.repository;

import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.ServiceBookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolioItemRepository extends JpaRepository<FolioItemEntity, Long> {
    Optional<FolioItemEntity> findByServiceBookingEntityAndIsActiveTrue(ServiceBookingEntity serviceBooking);

    List<FolioItemEntity> findByFolioEntity_IdAndIsActiveTrue(Long id);
}
