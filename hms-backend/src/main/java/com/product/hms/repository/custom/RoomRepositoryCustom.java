package com.product.hms.repository.custom;

import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.enums.RoomStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface RoomRepositoryCustom {
    Map<Long, Integer> countAvailableRoomsByRoomClass(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    );

    List<RoomEntity> findAvailableRoomsForPeriod(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    );

    List<RoomEntity> findAvailableRoomsForPeriodByRoomClassId(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            Long roomClassId,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    );
}
