package com.product.hms.repository;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.ReservationRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolioRepository extends JpaRepository<FolioEntity, Long> {

    /**
     * Tìm kiếm FolioEntity theo ReservationRoomEntity.
     *
     * @param reservationRoomEntity đối tượng ReservationRoomEntity cần tìm kiếm
     * @return Optional chứa FolioEntity nếu tìm thấy, hoặc Optional.empty() nếu không tìm thấy
     */
    Optional<FolioEntity> findByReservationRoomEntity(ReservationRoomEntity reservationRoomEntity);
    Optional<FolioEntity> findByReservationRoomEntityId(Long reservationRoomId);
}
