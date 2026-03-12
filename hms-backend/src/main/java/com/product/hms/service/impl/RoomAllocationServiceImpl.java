package com.product.hms.service.impl;

import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.request.RoomClassQuantityRequest;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.enums.ReservationRoomStatus;
import com.product.hms.repository.ReservationRoomRepository;
import com.product.hms.service.RoomAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoomAllocationServiceImpl implements RoomAllocationService {
    private final ReservationRoomRepository reservationRoomRepository;

    @Override
    public List<ReservationRoomEntity> createRoomAllocations(
            ReservationEntity reservationEntity,
            ReservationRequest reservationRequest,
            Map<Long, RoomClassEntity> roomClassById
    ) {
        List<ReservationRoomEntity> allocations = new ArrayList<>();
        for (RoomClassQuantityRequest roomClassQuantity : reservationRequest.roomClassQuantities()) {
            RoomClassEntity roomClass = roomClassById.get(roomClassQuantity.roomClassId());
            ReservationRoomEntity reservationRoomEntity = getReservationRoomEntity(reservationEntity, roomClassQuantity, roomClass);
            ReservationRoomEntity savedAllocation = reservationRoomRepository.save(reservationRoomEntity);
            allocations.add(savedAllocation);
        }
        return allocations;
    }

    private ReservationRoomEntity getReservationRoomEntity(ReservationEntity reservationEntity, RoomClassQuantityRequest roomClassQuantity, RoomClassEntity roomClass) {
        ReservationRoomEntity reservationRoomEntity = new ReservationRoomEntity();
        reservationRoomEntity.setReservationEntity(reservationEntity);
        reservationRoomEntity.setRoomClassEntity(roomClass);
        reservationRoomEntity.setNumberOfPeople(roomClassQuantity.numberOfPeople());
        reservationRoomEntity.setPriceAtBooking(roomClass.getBasePrice());
        reservationRoomEntity.setStatus(ReservationRoomStatus.PENDING);
        reservationRoomEntity.setIsActive(true);
        return reservationRoomEntity;
    }

    @Override
    public List<ReservationRoomEntity> getAllocationsByReservation(ReservationEntity reservationEntity) {
        return reservationRoomRepository.findByReservationEntity(reservationEntity);
    }

    @Override
    public void deleteAllocationsByReservation(ReservationEntity reservationEntity) {
        reservationRoomRepository.deleteByReservationEntity(reservationEntity);
    }
}
