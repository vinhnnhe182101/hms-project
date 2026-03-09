package com.product.hms.repository.custom.impl;

import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.enums.RoomStatus;
import com.product.hms.repository.custom.RoomRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RoomRepositoryCustomImpl implements RoomRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private Map<Long, Integer> executeCountRoomsByRoomClassQuery(TypedQuery<Object[]> typedQuery) {
        List<Object[]> queryResults = typedQuery.getResultList();
        Map<Long, Integer> roomsCountByRoomClass = new HashMap<>();
        queryResults.forEach(row -> {
            Long roomClassId = (Long) row[0];
            Integer count = ((Long) row[1]).intValue();
            roomsCountByRoomClass.put(roomClassId, count);
        });
        return roomsCountByRoomClass;
    }

    private Map<Long, Integer> countAllRoomsByRoomClass(RoomStatus roomAvailableStatus) {
        /*
         * JPQL: Đếm tổng số lượng phòng theo từng loại phòng
         * - Chỉ đếm các phòng có isActive = true và status = roomAvailableStatus
         * - Kết quả trả về là List<Object[]> với mỗi phần tử là [roomClassId, totalCount]
         */
        String jpql = """
                SELECT roomClassEntity.id, COUNT(roomEntity.id)
                FROM RoomEntity roomEntity
                JOIN roomEntity.roomClassEntity roomClassEntity
                WHERE roomEntity.isActive = true
                  AND roomEntity.status = :roomAvailableStatus
                  AND roomClassEntity.isActive = true
                GROUP BY roomClassEntity.id
                """;
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("roomAvailableStatus", roomAvailableStatus);

        return executeCountRoomsByRoomClassQuery(query);
    }

    private Map<Long, Integer> countBlockedRoomsByRoomClass(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    ) {
        /*
         * JPQL: Đếm số lượng phòng đã được đặt trong khoảng thời gian
         * - Chỉ đếm các phòng có isActive = true và reservationEntity.status IN (:reservationConfirmedStatus, :reservationInHouseStatus)
         * - Điều kiện thời gian: NOT (reservationEntity.expectedCheckOut <= :checkInDate OR reservationEntity.expectedCheckIn >= :checkOutDate)
         * - Kết quả trả về là List<Object[]> với mỗi phần tử là [roomClassId, blockedCount]
         */
        String jpql = """
                SELECT roomClassEntity.id, COUNT(DISTINCT reservationRoomEntity.id)
                FROM ReservationEntity reservationEntity
                JOIN reservationEntity.reservationRoomEntities reservationRoomEntity
                JOIN reservationRoomEntity.roomClassEntity roomClassEntity
                WHERE reservationEntity.isActive = true
                  AND reservationRoomEntity.isActive = true
                  AND reservationEntity.status IN (:reservationConfirmedStatus, :reservationInHouseStatus)
                  AND NOT (
                      reservationEntity.expectedCheckOut <= :checkInDate
                      OR reservationEntity.expectedCheckIn >= :checkOutDate
                  )
                GROUP BY roomClassEntity.id
                """;
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("checkInDate", checkInDate);
        query.setParameter("checkOutDate", checkOutDate);
        query.setParameter("reservationConfirmedStatus", reservationConfirmedStatus);
        query.setParameter("reservationInHouseStatus", reservationInHouseStatus);

        return executeCountRoomsByRoomClassQuery(query);
    }

    @Override
    public Map<Long, Integer> countAvailableRoomsByRoomClass(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    ) {

        // STEP 1: Đếm tổng số lượng phòng theo từng loại phòng (room class)
        Map<Long, Integer> totalRoomsByRoomClass = countAllRoomsByRoomClass(roomAvailableStatus);

        // STEP 2: Đếm số lượng phòng đã bị đặt (blocked) theo từng loại phòng trong khoảng thời gian
        Map<Long, Integer> blockedRoomsByRoomClass = countBlockedRoomsByRoomClass(
                checkInDate,
                checkOutDate,
                reservationConfirmedStatus,
                reservationInHouseStatus
        );

        // STEP 3: Tính số lượng phòng còn trống theo từng loại phòng
        Map<Long, Integer> availableRoomsByRoomClass = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : totalRoomsByRoomClass.entrySet()) {
            Long roomClassId = entry.getKey();
            Integer totalCount = entry.getValue();
            Integer blockedCount = blockedRoomsByRoomClass.getOrDefault(roomClassId, 0);
            Integer availableCount = Math.max(totalCount - blockedCount, 0);
            availableRoomsByRoomClass.put(roomClassId, availableCount);
        }

        return availableRoomsByRoomClass;
    }

    @Override
    public List<RoomEntity> findAvailableRoomsForPeriod(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    ) {
        // JPQL: Lấy danh sách phòng còn trống trong khoảng thời gian
        String jpql = """
                SELECT roomEntity
                FROM RoomEntity roomEntity
                JOIN FETCH roomEntity.roomClassEntity roomClassEntity
                WHERE roomEntity.isActive = true
                  AND roomClassEntity.isActive = true
                  AND roomEntity.status = :roomAvailableStatus
                  AND roomEntity.id NOT IN (
                      SELECT reservationRoomEntity.roomEntity.id
                      FROM ReservationRoomEntity reservationRoomEntity
                      JOIN reservationRoomEntity.reservationEntity reservationEntity
                      WHERE reservationRoomEntity.roomEntity IS NOT NULL
                        AND reservationRoomEntity.isActive = true
                        AND reservationEntity.isActive = true
                        AND reservationEntity.status IN (:reservationConfirmedStatus, :reservationInHouseStatus)
                        AND NOT (
                            reservationEntity.expectedCheckOut <= :checkInDate
                            OR reservationEntity.expectedCheckIn >= :checkOutDate
                        )
                  )
                ORDER BY roomClassEntity.id, roomEntity.id
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
        // JPQL: Lấy danh sách phòng còn trống theo loại phòng
        String jpql = """
                SELECT roomEntity
                FROM RoomEntity roomEntity
                JOIN FETCH roomEntity.roomClassEntity roomClassEntity
                WHERE roomEntity.isActive = true
                  AND roomClassEntity.isActive = true
                  AND roomClassEntity.id = :roomClassId
                  AND roomEntity.status = :roomAvailableStatus
                  AND roomEntity.id NOT IN (
                      SELECT reservationRoomEntity.roomEntity.id
                      FROM ReservationRoomEntity reservationRoomEntity
                      JOIN reservationRoomEntity.reservationEntity reservationEntity
                      WHERE reservationRoomEntity.roomEntity IS NOT NULL
                        AND reservationRoomEntity.isActive = true
                        AND reservationEntity.isActive = true
                        AND reservationEntity.status IN (:reservationConfirmedStatus, :reservationInHouseStatus)
                        AND NOT (
                            reservationEntity.expectedCheckOut <= :checkInDate
                            OR reservationEntity.expectedCheckIn >= :checkOutDate
                        )
                  )
                ORDER BY roomEntity.id
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
