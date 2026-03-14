package com.product.hms.repository;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.ServiceBookingEntity;
import com.product.hms.enums.FolioItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    // Find items by folio
    List<FolioItemEntity> findByFolioEntityId(Long folioId);

    // Find items by folio and type
    List<FolioItemEntity> findByFolioEntityIdAndType(Long folioId, FolioItemType type);

    // Get total by staff and type in period
    @Query("SELECT COALESCE(SUM(fi.totalPrice), 0) FROM FolioItemEntity fi " +
            "JOIN fi.folioEntity f " +
            "JOIN f.reservationRoomEntity rr " +
            "JOIN rr.roomEntity r " +
            "JOIN HousekeepingTaskEntity t ON t.roomEntity.id = r.id " +
            "WHERE t.assigneeEntity.id = :staffId " +
            "AND fi.type = :type " +
            "AND fi.createdAt BETWEEN :start AND :end")
    Optional<BigDecimal> getTotalByStaffAndTypeAndPeriod(
            @Param("staffId") Long staffId,
            @Param("type") FolioItemType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Find unpaid items by folio
    List<FolioItemEntity> findByFolioEntityIdAndStatus(Long folioId, String status);
}
