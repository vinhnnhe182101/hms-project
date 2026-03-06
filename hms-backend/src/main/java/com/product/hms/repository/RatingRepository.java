package com.product.hms.repository;

import com.product.hms.entity.RatingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {

    @Query("SELECT r FROM RatingEntity r " +
            "JOIN r.reservationEntity res " +
            "JOIN res.reservationRoomAllocationEntities rra " +
            "WHERE rra.roomClassEntity.id = :roomClassId " +
            "AND r.isPublic = true " +
            "ORDER BY r.reviewDate DESC")
    Page<RatingEntity> findPublicRatingsByRoomClassId(@Param("roomClassId") Long roomClassId, Pageable pageable);

    @Query("SELECT r FROM RatingEntity r " +
            "JOIN r.reservationEntity res " +
            "JOIN res.reservationRoomAllocationEntities rra " +
            "WHERE rra.roomClassEntity.id = :roomClassId " +
            "AND r.isPublic = true " +
            "AND r.rating = :rating " +
            "ORDER BY r.reviewDate DESC")
    Page<RatingEntity> findPublicRatingsByRoomClassIdAndRating(
            @Param("roomClassId") Long roomClassId,
            @Param("rating") Integer rating,
            Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM RatingEntity r " +
            "JOIN r.reservationEntity res " +
            "JOIN res.reservationRoomAllocationEntities rra " +
            "WHERE rra.roomClassEntity.id = :roomClassId " +
            "AND r.isPublic = true")
    Double getAverageRatingByRoomClassId(@Param("roomClassId") Long roomClassId);

    @Query("SELECT COUNT(r) FROM RatingEntity r " +
            "JOIN r.reservationEntity res " +
            "JOIN res.reservationRoomAllocationEntities rra " +
            "WHERE rra.roomClassEntity.id = :roomClassId " +
            "AND r.isPublic = true")
    Long countPublicRatingsByRoomClassId(@Param("roomClassId") Long roomClassId);

    @Query("SELECT r.rating, COUNT(r) FROM RatingEntity r " +
            "JOIN r.reservationEntity res " +
            "JOIN res.reservationRoomAllocationEntities rra " +
            "WHERE rra.roomClassEntity.id = :roomClassId " +
            "AND r.isPublic = true " +
            "GROUP BY r.rating")
    List<Object[]> countRatingsByRatingForRoomClass(@Param("roomClassId") Long roomClassId);
}
