package com.product.hms.api;

import com.product.hms.dto.request.CreateRoomRequest;
import com.product.hms.dto.request.RoomSearchRequest;
import com.product.hms.dto.response.AvailableRoomResponse;
import com.product.hms.dto.response.RoomClassAvailabilityResponse;
import com.product.hms.dto.response.RoomClassAvailableRoomsResponse;
import com.product.hms.dto.response.RoomResponse;
import com.product.hms.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/search")
    public ResponseEntity<Page<RoomResponse>> search(
            RoomSearchRequest filter,
            Pageable pageable
    ) {
        Page<RoomResponse> result = roomService.search(filter, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody CreateRoomRequest request
    ) {
        RoomResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

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

    /**
     * Get available physical rooms grouped by room class for manual assignment.
     */
    @GetMapping("/available-for-assignment")
    public ResponseEntity<Map<String, List<RoomClassAvailableRoomsResponse>>> getAvailableRoomsForAssignment(
            @RequestParam("checkInDate") Long checkInDate,
            @RequestParam("checkOutDate") Long checkOutDate
    ) {
        Timestamp checkIn = new Timestamp(checkInDate);
        Timestamp checkOut = new Timestamp(checkOutDate);

        List<RoomClassAvailableRoomsResponse> roomClassAvailableRooms =
                roomService.getAvailableRoomsForAssignment(checkIn, checkOut);

        Map<String, List<RoomClassAvailableRoomsResponse>> response =
                Map.of("roomClassAvailableRoomsResponses", roomClassAvailableRooms);
        return ResponseEntity.ok(response);
    }

    /**
     * Get available physical rooms for one room class (manual assignment use-case).
     */
    @GetMapping("/available-for-assignment/by-room-class")
    public ResponseEntity<Map<String, List<AvailableRoomResponse>>> getAvailableRoomsByRoomClassIdForAssignment(
            @RequestParam("roomClassId") Long roomClassId,
            @RequestParam("checkInDate") Long checkInDate,
            @RequestParam("checkOutDate") Long checkOutDate
    ) {
        Timestamp checkIn = new Timestamp(checkInDate);
        Timestamp checkOut = new Timestamp(checkOutDate);

        List<AvailableRoomResponse> availableRooms = roomService
                .getAvailableRoomsByRoomClassIdForAssignment(roomClassId, checkIn, checkOut);

        return ResponseEntity.ok(Map.of("availableRooms", availableRooms));
    }
}
