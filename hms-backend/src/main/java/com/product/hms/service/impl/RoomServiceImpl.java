package com.product.hms.service.impl;

import com.product.hms.converters.RoomClassMapper;
import com.product.hms.dto.response.AvailableRoomResponse;
import com.product.hms.dto.response.RoomClassAvailabilityResponse;
import com.product.hms.dto.response.RoomClassAvailableRoomsResponse;
import com.product.hms.dto.response.RoomMatrixResponse;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.entity.RoomEntity;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.RoomClassRepository;
import com.product.hms.repository.RoomRepository;
import com.product.hms.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of RoomService
 */
@Service
@Slf4j
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
                .collect(Collectors.groupingBy(
                        room -> room.getRoomClassEntity().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
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


    private String extractFloor(String roomNumber) {
        if (roomNumber == null || roomNumber.length() <= 2) {
            return "1";
        }
        return roomNumber.substring(0, roomNumber.length() - 2);
    }

    @Override
    @Cacheable(value = "availableFloors") // Kích hoạt bộ nhớ đệm cho danh sách tầng
    public List<String> getAvailableFloors() {
        log.info("Fetching available floors from active rooms (Missed Cache - Querying DB)");
        try {
            List<String> floors = roomRepository.findByIsActiveTrue().stream()
                    .map(room -> extractFloor(room.getRoomNumber()))
                    .distinct()
                    .sorted((f1, f2) -> {
                        // 1. Tách phần chữ (Alphabet) và phần số (Numeric) của Tầng 1
                        String alpha1 = f1.replaceAll("[0-9]", ""); // Chỉ lấy chữ
                        String numStr1 = f1.replaceAll("[^0-9]", ""); // Chỉ lấy số
                        int num1 = numStr1.isEmpty() ? 0 : Integer.parseInt(numStr1);

                        // 2. Tách phần chữ và phần số của Tầng 2
                        String alpha2 = f2.replaceAll("[0-9]", "");
                        String numStr2 = f2.replaceAll("[^0-9]", "");
                        int num2 = numStr2.isEmpty() ? 0 : Integer.parseInt(numStr2);

                        // 3. Ưu tiên so sánh phần chữ trước (VD: Tầng số (rỗng "") < Tầng A < Tầng B)
                        int alphaCompare = alpha1.compareToIgnoreCase(alpha2);
                        if (alphaCompare != 0) {
                            return alphaCompare;
                        }

                        // 4. Nếu phần chữ giống nhau, so sánh theo số (VD: A2 < A10)
                        return Integer.compare(num1, num2);
                    })
                    .toList();

            log.info("Found {} available floors: {}", floors.size(), floors);
            return floors;
        } catch (Exception e) {
            log.error("Error retrieving available floors", e);
            throw new RuntimeException("Failed to retrieve available floors", e);
        }
    }

    @Override
    public List<RoomMatrixResponse> getRoomStatusMatrixByFloor(String floor) {
        log.info("Retrieving room status matrix for floor: {}", floor);
        try {
            // Determine target floor: if null/blank, use first available floor
            String targetFloor = floor;
            if (floor == null || floor.isBlank()) {
                log.info("Floor parameter is null or blank, fetching first available floor");
                List<String> availableFloors = getAvailableFloors();

                if (availableFloors.isEmpty()) {
                    log.warn("No available floors found in the system");
                    return List.of();
                }

                targetFloor = availableFloors.get(0);
                log.info("Using first available floor: {}", targetFloor);
            }

            // Fetch and filter rooms for the target floor
            final String floorToMatch = targetFloor;
            List<RoomMatrixResponse> roomMatrix = roomRepository.findByIsActiveTrue().stream()
                    .filter(room -> extractFloor(room.getRoomNumber()).equals(floorToMatch))
                    .sorted(Comparator.comparing(RoomEntity::getRoomNumber))
                    .map(this::mapToRoomMatrixResponse)
                    .toList();

            log.info("Found {} rooms on floor '{}'", roomMatrix.size(), targetFloor);
            return roomMatrix;
        } catch (Exception e) {
            log.error("Error retrieving room status matrix for floor: {}", floor, e);
            throw new RuntimeException("Failed to retrieve room matrix for floor: " + floor, e);
        }
    }

    /**
     * Private helper method to map RoomEntity to RoomMatrixResponse DTO.
     * Safely extracts room class name with fallback to "N/A".
     *
     * @param room the RoomEntity to map
     * @return RoomMatrixResponse DTO
     */
    private RoomMatrixResponse mapToRoomMatrixResponse(RoomEntity room) {
        String roomClassName = "N/A";

        if (room.getRoomClassEntity() != null && room.getRoomClassEntity().getName() != null) {
            roomClassName = room.getRoomClassEntity().getName();
        }

        return RoomMatrixResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .status(room.getStatus() != null ? room.getStatus().toString() : "UNKNOWN")
                .roomClassName(roomClassName)
                .build();
    }
}
