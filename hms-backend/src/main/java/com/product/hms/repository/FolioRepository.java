package com.product.hms.repository;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.ReservationRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    // Find folio by reservation room
    Optional<FolioEntity> findByReservationRoomEntityId(Long reservationRoomId);

    // Find folio by reservation
    @Query("SELECT f FROM FolioEntity f WHERE f.reservationRoomEntity.reservationEntity.id = :reservationId")
    Optional<FolioEntity> findByReservationEntityId(@Param("reservationId") Long reservationId);

    // Find folio by room and status
    @Query("SELECT f FROM FolioEntity f WHERE f.reservationRoomEntity.roomEntity.id = :roomId " +
            "AND f.status != 'SETTLED'")
    Optional<FolioEntity> findActiveFolioByRoomId(@Param("roomId") Long roomId);
}
