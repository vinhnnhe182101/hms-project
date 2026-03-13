package com.product.hms.repository.custom;

import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.enums.RoomStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface RoomRepositoryCustom {
    /**
     * Đếm số lượng phòng còn trống theo từng loại phòng (room class) trong khoảng thời gian chỉ định.
     *
     * @param checkInDate                ngày nhận phòng
     * @param checkOutDate               ngày trả phòng
     * @param roomAvailableStatus        trạng thái phòng còn trống
     * @param reservationConfirmedStatus trạng thái đặt phòng đã xác nhận
     * @param reservationInHouseStatus   trạng thái đang ở
     * @return Map(roomClassId, số lượng phòng còn trống)
     */
    Map<Long, Integer> countAvailableRoomsByRoomClass(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    );

    /**
     * Lấy danh sách phòng còn trống trong khoảng thời gian chỉ định.
     *
     * @param checkInDate                ngày nhận phòng
     * @param checkOutDate               ngày trả phòng
     * @param roomAvailableStatus        trạng thái phòng còn trống
     * @param reservationConfirmedStatus trạng thái đặt phòng đã xác nhận
     * @param reservationInHouseStatus   trạng thái đang ở
     * @return List<RoomEntity> danh sách phòng còn trống
     */
    List<RoomEntity> findAvailableRoomsForPeriod(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    );

    /**
     * Lấy danh sách phòng còn trống theo loại phòng (roomClassId) trong khoảng thời gian chỉ định.
     *
     * @param checkInDate                ngày nhận phòng
     * @param checkOutDate               ngày trả phòng
     * @param roomClassId                id loại phòng
     * @param roomAvailableStatus        trạng thái phòng còn trống
     * @param reservationConfirmedStatus trạng thái đặt phòng đã xác nhận
     * @param reservationInHouseStatus   trạng thái đang ở
     * @return List<RoomEntity> Danh sách phòng còn trống theo loại phòng
     */
    List<RoomEntity> findAvailableRoomsForPeriodByRoomClassId(
            Timestamp checkInDate,
            Timestamp checkOutDate,
            Long roomClassId,
            RoomStatus roomAvailableStatus,
            ReservationStatus reservationConfirmedStatus,
            ReservationStatus reservationInHouseStatus
    );
}
