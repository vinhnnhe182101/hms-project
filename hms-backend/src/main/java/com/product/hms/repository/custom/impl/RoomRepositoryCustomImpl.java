package com.product.hms.repository.custom.impl;

import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.enums.RoomStatus;
import com.product.hms.repository.custom.RoomRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RoomRepositoryCustomImpl implements RoomRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Map<Long, Integer> countAvailableRoomsByRoomClass(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    ) {
        String jpql = """
                SELECT rc.id, COUNT(DISTINCT r.id)
                FROM RoomEntity r
                JOIN r.roomClassEntity rc
                WHERE r.isActive = true
                  AND r.status = :roomAvailableStatus
                  AND rc.isActive = true
                  AND r.id NOT IN (
                      SELECT DISTINCT rr.roomEntity.id
                      FROM ReservationRoomEntity rr
                      JOIN rr.reservationEntity res
                      WHERE rr.roomEntity IS NOT NULL
                        AND res.isActive = true
                        AND rr.isActive = true
                        AND res.status IN (:reservationConfirmedStatus, :reservationInHouseStatus)
                        AND NOT (
                            res.expectedCheckOut <= :checkInDate
                            OR res.expectedCheckIn >= :checkOutDate
                        )
                  )
                GROUP BY rc.id
                """;

        List<Object[]> rows = entityManager.createQuery(jpql, Object[].class)
                .setParameter("checkInDate", checkInDate)
                .setParameter("checkOutDate", checkOutDate)
                .setParameter("roomAvailableStatus", roomAvailableStatus)
                .setParameter("reservationConfirmedStatus", reservationConfirmedStatus)
                .setParameter("reservationInHouseStatus", reservationInHouseStatus)
                .getResultList();

        Map<Long, Integer> result = new HashMap<>();
        for (Object[] row : rows) {
            Long roomClassId = (Long) row[0];
            Integer availableCount = ((Long) row[1]).intValue();
            result.put(roomClassId, availableCount);
        }
        return result;
    }

    @Override
    public List<RoomEntity> findAvailableRoomsForPeriod(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    ) {
        String jpql = """
                SELECT r
                FROM RoomEntity r
                JOIN FETCH r.roomClassEntity rc
                WHERE r.isActive = true
                  AND rc.isActive = true
                  AND r.status = :roomAvailableStatus
                  AND r.id NOT IN (
                      SELECT rr.roomEntity.id
                      FROM ReservationRoomEntity rr
                      JOIN rr.reservationEntity res
                      WHERE rr.roomEntity IS NOT NULL
                        AND rr.isActive = true
                        AND res.isActive = true
                        AND res.status IN (:reservationConfirmedStatus, :reservationInHouseStatus)
                        AND NOT (
                            res.expectedCheckOut <= :checkInDate
                            OR res.expectedCheckIn >= :checkOutDate
                        )
                  )
                ORDER BY rc.id, r.id
                """;

        return entityManager.createQuery(jpql, RoomEntity.class)
                .setParameter("checkInDate", checkInDate)
                .setParameter("checkOutDate", checkOutDate)
                .setParameter("roomAvailableStatus", roomAvailableStatus)
                .setParameter("reservationConfirmedStatus", reservationConfirmedStatus)
                .setParameter("reservationInHouseStatus", reservationInHouseStatus)
                .getResultList();
    }

    @Override
    public List<RoomEntity> findAvailableRoomsForPeriodByRoomClassId(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            Long roomClassId,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    ) {
        String jpql = """
                SELECT r
                FROM RoomEntity r
                JOIN FETCH r.roomClassEntity rc
                WHERE r.isActive = true
                  AND rc.isActive = true
                  AND rc.id = :roomClassId
                  AND r.status = :roomAvailableStatus
                  AND r.id NOT IN (
                      SELECT rr.roomEntity.id
                      FROM ReservationRoomEntity rr
                      JOIN rr.reservationEntity res
                      WHERE rr.roomEntity IS NOT NULL
                        AND rr.isActive = true
                        AND res.isActive = true
                        AND res.status IN (:reservationConfirmedStatus, :reservationInHouseStatus)
                        AND NOT (
                            res.expectedCheckOut <= :checkInDate
                            OR res.expectedCheckIn >= :checkOutDate
                        )
                  )
                ORDER BY r.id
                """;

        return entityManager.createQuery(jpql, RoomEntity.class)
                .setParameter("checkInDate", checkInDate)
                .setParameter("checkOutDate", checkOutDate)
                .setParameter("roomClassId", roomClassId)
                .setParameter("roomAvailableStatus", roomAvailableStatus)
                .setParameter("reservationConfirmedStatus", reservationConfirmedStatus)
                .setParameter("reservationInHouseStatus", reservationInHouseStatus)
                .getResultList();
    }
}
