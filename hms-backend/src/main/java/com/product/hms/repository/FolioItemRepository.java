package com.product.hms.repository;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.ServiceBookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolioItemRepository extends JpaRepository<FolioItemEntity, Long> {
    /**
     * Tìm kiếm FolioItemEntity theo ServiceBookingEntity và isActive = true.
     *
     * @param serviceBookingEntity đối tượng ServiceBookingEntity cần tìm kiếm
     * @return Optional chứa FolioItemEntity nếu tìm thấy, hoặc Optional.empty() nếu không tìm thấy
     */
    Optional<FolioItemEntity> findByServiceBookingEntityAndIsActiveTrue(ServiceBookingEntity serviceBookingEntity);

    /**
     * Tìm kiếm danh sách FolioItemEntity theo FolioEntity và isActive = true.
     *
     * @param folioEntity đối tượng FolioEntity cần tìm kiếm
     * @return Danh sách FolioItemEntity thỏa mãn điều kiện
     */
    List<FolioItemEntity> findByFolioEntityAndIsActiveTrue(FolioEntity folioEntity);

    /**
     * Tìm kiếm danh sách FolioItemEntity theo id của FolioEntity và isActive = true.
     *
     * @param folioId id của FolioEntity cần tìm kiếm
     * @return Danh sách FolioItemEntity thỏa mãn điều kiện
     */
    List<FolioItemEntity> findByFolioEntity_IdAndIsActiveTrue(Long folioId);
}
