package com.product.hms.service;

import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomClassEntity;

import java.util.List;
import java.util.Map;

public interface RoomAllocationService {
    List<ReservationRoomEntity> createRoomAllocations(
            ReservationEntity reservation,
            ReservationRequest request,
            Map<Long, RoomClassEntity> roomClassById
    );

    List<ReservationRoomEntity> getAllocationsByReservation(ReservationEntity reservation);

    void deleteAllocationsByReservation(ReservationEntity reservation);
}
