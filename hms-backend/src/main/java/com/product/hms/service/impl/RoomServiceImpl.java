package com.product.hms.service.impl;

import com.product.hms.converters.RoomClassMapper;
import com.product.hms.dto.response.AvailableRoomResponse;
import com.product.hms.dto.response.RoomClassAvailabilityResponse;
import com.product.hms.dto.response.RoomClassAvailableRoomsResponse;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.entity.RoomEntity;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.RoomClassRepository;
import com.product.hms.repository.RoomRepository;
import com.product.hms.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of RoomService
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomClassRepository roomClassRepository;
    private final RoomClassMapper roomClassMapper;

    @Override
    public List<RoomClassAvailabilityResponse> getAvailableRooms(Timestamp checkInDate, Timestamp checkOutDate) {
        validateDateRange(checkInDate, checkOutDate);

        Map<Long, Integer> availableRoomsMap = roomRepository.countAvailableRoomsByRoomClass(
                checkInDate,
                checkOutDate
        );

        // Get all active room classes
        List<RoomClassEntity> allRoomClasses = roomClassRepository.findAll().stream()
                .filter(RoomClassEntity::getIsActive)
                .toList();

        // Build response - return List directly
        return allRoomClasses.stream()
                .map(roomClass -> {
                    Integer availableCount = availableRoomsMap.getOrDefault(roomClass.getId(), 0);
                    return new RoomClassAvailabilityResponse(roomClassMapper.toResponse(roomClass), availableCount);
                })
                .toList();
    }

    @Override
    public List<RoomClassAvailableRoomsResponse> getAvailableRoomsForAssignment(Timestamp checkInDate, Timestamp checkOutDate) {
        validateDateRange(checkInDate, checkOutDate);

        List<RoomEntity> availableRooms = roomRepository.findAvailableRoomsForPeriod(
                checkInDate,
                checkOutDate
        );

        Map<Long, List<RoomEntity>> roomsByClassId = availableRooms.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        room -> room.getRoomClassEntity().getId(),
                        LinkedHashMap::new,
                        java.util.stream.Collectors.toList()
                ));

        return roomsByClassId.values().stream()
                .map(classRooms -> {
                    RoomClassEntity roomClass = classRooms.get(0).getRoomClassEntity();
                    List<AvailableRoomResponse> availableRoomResponses = classRooms.stream()
                            .map(room -> new AvailableRoomResponse(room.getId(), room.getRoomNumber()))
                            .toList();
                    return new RoomClassAvailableRoomsResponse(
                            roomClassMapper.toResponse(roomClass),
                            availableRoomResponses
                    );
                })
                .toList();
    }

    @Override
    public List<AvailableRoomResponse> getAvailableRoomsByRoomClassIdForAssignment(
            Long roomClassId,
            Timestamp checkInDate,
            Timestamp checkOutDate
    ) {
        validateDateRange(checkInDate, checkOutDate);
        RoomClassEntity roomClass = validateAndGetRoomClass(roomClassId);

        return roomRepository.findAvailableRoomsForPeriodByRoomClassId(
                        checkInDate,
                        checkOutDate,
                        roomClass.getId()
                )
                .stream()
                .map(room -> new AvailableRoomResponse(room.getId(), room.getRoomNumber()))
                .toList();
    }

    private RoomClassEntity validateAndGetRoomClass(Long roomClassId) {
        if (roomClassId == null) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "roomClassId must be provided");
        }

        RoomClassEntity roomClass = roomClassRepository.findById(roomClassId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.ROOM_CLASS_NOT_FOUND,
                        "Room class not found with ID: " + roomClassId
                ));

        if (!Boolean.TRUE.equals(roomClass.getIsActive())) {
            throw new BadRequestException(
                    ErrorCode.ROOM_CLASS_INACTIVE,
                    "Room class is inactive: " + roomClassId
            );
        }
        return roomClass;
    }

    private void validateDateRange(Timestamp checkInDate, Timestamp checkOutDate) {
        if (checkInDate == null || checkOutDate == null || !checkOutDate.after(checkInDate)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_DATE_RANGE,
                    "checkOutDate must be after checkInDate"
            );
        }
    }
}
