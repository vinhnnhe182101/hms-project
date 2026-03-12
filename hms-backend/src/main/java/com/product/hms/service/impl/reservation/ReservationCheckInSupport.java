package com.product.hms.service.impl.reservation;

import com.product.hms.dto.request.ReservationCheckInRequest;
import com.product.hms.dto.request.ReservationRoomCheckInRequest;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomEntity;
import com.product.hms.enums.ReservationRoomStatus;
import com.product.hms.enums.RoomStatus;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.ReservationRoomRepository;
import com.product.hms.repository.RoomRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public final class ReservationCheckInSupport {

    private ReservationCheckInSupport() {
    }

    public static void assignRoomsForCheckIn(
            Long reservationId,
            ReservationCheckInRequest request,
            ReservationRoomRepository reservationRoomRepository,
            RoomRepository roomRepository
    ) {
        List<ReservationRoomEntity> allocations = reservationRoomRepository
                .findByReservationEntity_IdAndIsActiveTrue(reservationId);

        if (allocations.isEmpty()) {
            throw new BusinessException(
                    "Reservation has no active room allocations for check-in"
            );
        }

        Map<Long, Long> roomIdByAllocationId = extractManualAssignmentMap(reservationId, request, allocations);
        executeAssignment(allocations, roomIdByAllocationId, Boolean.TRUE.equals(request.autoAssign()), roomRepository);

        roomRepository.saveAll(allocations.stream().map(ReservationRoomEntity::getRoomEntity).toList());
        reservationRoomRepository.saveAll(allocations);
    }

    private static Map<Long, Long> extractManualAssignmentMap(
            Long reservationId,
            ReservationCheckInRequest request,
            List<ReservationRoomEntity> allocations
    ) {
        Map<Long, Long> roomIdByAllocationId = new HashMap<>();
        Set<Long> validAllocationIds = allocations.stream()
                .map(ReservationRoomEntity::getId)
                .collect(Collectors.toSet());

        List<ReservationRoomCheckInRequest> assignments = request.roomAssignments() == null
                ? List.of()
                : request.roomAssignments();

        for (ReservationRoomCheckInRequest assignment : assignments) {
            if (assignment == null || assignment.reservationRoomId() == null || assignment.roomId() == null) {
                throw new BadRequestException(
                        ErrorCode.INVALID_REQUEST,
                        "Each room assignment must include reservationRoomId and roomId"
                );
            }
            if (!validAllocationIds.contains(assignment.reservationRoomId())) {
                throw new BadRequestException(
                        ErrorCode.INVALID_REQUEST,
                        "reservationRoomId does not belong to reservation " + reservationId + ": " + assignment.reservationRoomId()
                );
            }
            Long previous = roomIdByAllocationId.putIfAbsent(assignment.reservationRoomId(), assignment.roomId());
            if (previous != null) {
                throw new BadRequestException(
                        ErrorCode.INVALID_REQUEST,
                        "Duplicate assignment for reservationRoomId: " + assignment.reservationRoomId()
                );
            }
        }

        return roomIdByAllocationId;
    }

    private static void executeAssignment(
            List<ReservationRoomEntity> allocations,
            Map<Long, Long> roomIdByAllocationId,
            boolean autoAssign,
            RoomRepository roomRepository
    ) {
        Set<Long> usedRoomIds = new HashSet<>();

        for (ReservationRoomEntity allocation : allocations) {
            RoomEntity room = resolveRoomForAllocation(
                    allocation,
                    roomIdByAllocationId.get(allocation.getId()),
                    autoAssign,
                    usedRoomIds,
                    roomRepository
            );

            allocation.setRoomEntity(room);
            allocation.setStatus(ReservationRoomStatus.CHECKED_IN);
            allocation.setActualCheckIn(Instant.now());

            room.setStatus(RoomStatus.OCCUPIED);
            usedRoomIds.add(room.getId());
        }
    }

    private static RoomEntity resolveRoomForAllocation(
            ReservationRoomEntity allocation,
            Long manualRoomId,
            boolean autoAssign,
            Set<Long> usedRoomIds,
            RoomRepository roomRepository
    ) {
        if (manualRoomId != null) {
            RoomEntity room = roomRepository.findById(manualRoomId)
                    .orElseThrow(() -> new NotFoundException(
                            ErrorCode.ROOM_NOT_FOUND,
                            "Room not found with ID: " + manualRoomId
                    ));
            validateRoomAssignable(allocation, room, usedRoomIds);
            return room;
        }

        if (!autoAssign) {
            throw new BusinessException(
                    "Missing room assignment for reservationRoomId: " + allocation.getId()
            );
        }

        List<RoomEntity> availableRooms = roomRepository.findByRoomClassEntity_IdAndStatusAndIsActiveTrueOrderByIdAsc(
                allocation.getRoomClassEntity().getId(),
                RoomStatus.AVAILABLE
        );

        RoomEntity room = availableRooms.stream()
                .filter(candidate -> !usedRoomIds.contains(candidate.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "No available room for room class ID: " + allocation.getRoomClassEntity().getId()
                ));

        validateRoomAssignable(allocation, room, usedRoomIds);
        return room;
    }

    private static void validateRoomAssignable(ReservationRoomEntity allocation, RoomEntity room, Set<Long> usedRoomIds) throws BusinessException {
        if (usedRoomIds.contains(room.getId())) {
            throw new BadRequestException(
                    ErrorCode.INVALID_REQUEST,
                    "Room is assigned to multiple allocations in one check-in request: " + room.getId()
            );
        }

        if (!Boolean.TRUE.equals(room.getIsActive())) {
            throw new BusinessException("Room is inactive: " + room.getId());
        }

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new BusinessException("Room is not available: " + room.getId());
        }

        if (!room.getRoomClassEntity().getId().equals(allocation.getRoomClassEntity().getId())) {
            throw new BusinessException(
                    "Room class mismatch for reservationRoomId: " + allocation.getId()
            );
        }
    }
}

