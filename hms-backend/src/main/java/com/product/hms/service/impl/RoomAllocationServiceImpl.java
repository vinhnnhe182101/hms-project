package com.product.hms.service.impl;

import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.request.RoomClassQuantityRequest;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomAllocationEntity;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.repository.ReservationRoomAllocationRepository;
import com.product.hms.service.RoomAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoomAllocationServiceImpl implements RoomAllocationService {
    private final ReservationRoomAllocationRepository reservationRoomAllocationRepository;

    @Override
    public List<ReservationRoomAllocationEntity> createRoomAllocations(
            ReservationEntity reservation,
            ReservationRequest request,
            Map<Long, RoomClassEntity> roomClassById
    ) {
        List<ReservationRoomAllocationEntity> allocations = new ArrayList<>();
        for (RoomClassQuantityRequest roomClassQuantity : request.roomClassQuantities()) {
            RoomClassEntity roomClass = roomClassById.get(roomClassQuantity.roomClassId());
            ReservationRoomAllocationEntity allocation = new ReservationRoomAllocationEntity();
            allocation.setReservationEntity(reservation);
            allocation.setRoomClassEntity(roomClass);
            allocation.setNumberOfPeople(roomClassQuantity.numberOfPeople());
            allocation.setPriceAtBooking(roomClass.getBasePrice());
            allocation.setIsActive(true);
            ReservationRoomAllocationEntity savedAllocation = reservationRoomAllocationRepository.save(allocation);
            allocations.add(savedAllocation);
        }
        return allocations;
    }

    @Override
    public List<ReservationRoomAllocationEntity> getAllocationsByReservation(ReservationEntity reservation) {
        return reservationRoomAllocationRepository.findByReservationEntity(reservation);
    }

    @Override
    public void deleteAllocationsByReservation(ReservationEntity reservation) {
        reservationRoomAllocationRepository.deleteByReservationEntity(reservation);
    }
}
