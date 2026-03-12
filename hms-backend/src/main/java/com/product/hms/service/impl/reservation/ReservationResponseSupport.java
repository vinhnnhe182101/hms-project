package com.product.hms.service.impl.reservation;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.dto.response.RoomClassQuantityResponse;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.service.ReservationRoomService;

import java.util.ArrayList;
import java.util.List;

public final class ReservationResponseSupport {

    private ReservationResponseSupport() {
    }

    public static ReservationResponse buildReservationResponse(
            ReservationEntity reservation,
            CustomerEntity customer,
            ReservationRoomService reservationRoomService,
            CustomerMapper customerMapper
    ) {
        List<RoomClassQuantityResponse> allocationResponses = new ArrayList<>();
        List<ReservationRoomEntity> allocations = reservationRoomService.getAllocationsByReservation(reservation);

        for (ReservationRoomEntity allocation : allocations) {
            allocationResponses.add(new RoomClassQuantityResponse(
                    allocation.getId(),
                    allocation.getRoomClassEntity().getId(),
                    allocation.getNumberOfPeople()
            ));
        }

        return new ReservationResponse(
                reservation.getId(),
                reservation.getCode(),
                customerMapper.toResponse(customer),
                allocationResponses,
                reservation.getExpectedCheckIn(),
                reservation.getExpectedCheckOut(),
                reservation.getStatus().name(),
                reservation.getNumberOfMembers(),
                reservation.getNote(),
                reservation.getCreatedAt()
        );
    }
}
