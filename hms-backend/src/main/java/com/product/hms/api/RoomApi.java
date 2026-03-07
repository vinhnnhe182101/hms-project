package com.product.hms.api;

import com.product.hms.dto.response.RoomClassAvailabilityResponse;
import com.product.hms.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * REST API controller for room operations
 */
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomApi {

    private final RoomService roomService;

    /**
     * Get available rooms by room class for a given date range
     *
     * @param checkInDate  check-in date in timestamp format
     * @param checkOutDate check-out date in timestamp format
     * @return ResponseEntity containing map with available rooms list
     */
    @GetMapping("/available")
    public ResponseEntity<Map<String, List<RoomClassAvailabilityResponse>>> getAvailableRooms(
            @RequestParam("checkInDate") Long checkInDate,
            @RequestParam("checkOutDate") Long checkOutDate) {

        Timestamp checkIn = new Timestamp(checkInDate);
        Timestamp checkOut = new Timestamp(checkOutDate);

        List<RoomClassAvailabilityResponse> roomClassAvailabilities = roomService.getAvailableRooms(checkIn, checkOut);
        Map<String, List<RoomClassAvailabilityResponse>> response = Map.of("roomClassAvailabilityResponses", roomClassAvailabilities);
        return ResponseEntity.ok(response);
    }
}


