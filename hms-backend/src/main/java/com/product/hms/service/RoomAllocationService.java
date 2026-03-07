package com.product.hms.service;

import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomAllocationEntity;
import com.product.hms.entity.RoomClassEntity;

import java.util.List;
import java.util.Map;

public interface RoomAllocationService {
    List<ReservationRoomAllocationEntity> createRoomAllocations(
            ReservationEntity reservation,
            ReservationRequest request,
            Map<Long, RoomClassEntity> roomClassById
    );

    List<ReservationRoomAllocationEntity> getAllocationsByReservation(ReservationEntity reservation);

    void deleteAllocationsByReservation(ReservationEntity reservation);
}
