package com.product.hms.service.impl;

import com.product.hms.converters.RoomClassMapper;
import com.product.hms.dto.response.RoomClassAvailabilityResponse;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.repository.RoomClassRepository;
import com.product.hms.repository.RoomRepository;
import com.product.hms.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
        // Get available room counts by room class
        Map<Long, Integer> availableRoomsMap = roomRepository.countAvailableRoomsByRoomClass(checkInDate, checkOutDate);

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
}

