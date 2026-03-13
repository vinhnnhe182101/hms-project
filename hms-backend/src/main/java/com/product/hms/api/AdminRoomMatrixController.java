package com.product.hms.api;

import com.product.hms.dto.response.RoomMatrixResponse;
import com.product.hms.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Room Matrix Management.
 * Provides endpoints for retrieving room status information grouped by floor.
 *
 * Base Path: /api/v1/admin/housekeeping
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/housekeeping")
@RequiredArgsConstructor
public class AdminRoomMatrixController {

    private final RoomService roomService;

    /**
     * GET /api/v1/admin/housekeeping/floors
     * Retrieve all available floors where active rooms exist.
     *
     * Returns floors sorted numerically:
     * - "1" comes before "2"
     * - "2" comes before "10"
     *
     * @return ResponseEntity with list of floor strings
     */
    @GetMapping("/floors")
    public ResponseEntity<List<String>> getAvailableFloors() {
        log.info("GET request: Fetching available floors");
        try {
            List<String> floors = roomService.getAvailableFloors();
            log.info("Successfully retrieved {} available floors", floors.size());
            return ResponseEntity.ok(floors);
        } catch (Exception e) {
            log.error("Error fetching available floors", e);
            throw e;
        }
    }

    /**
     * GET /api/v1/admin/housekeeping/rooms-matrix
     * Retrieve room status matrix for a specific floor or default to the first available floor.
     *
     * Query Parameters:
     * - floor (optional): Floor identifier (e.g., "1", "12")
     *   - If provided: Returns rooms on that floor
     *   - If not provided: Returns rooms on the first available floor
     *   - If no floors exist: Returns empty list
     *
     * Rooms are sorted by roomNumber in ascending order.
     * Each room includes: id, roomNumber, status, roomClassName
     *
     * @param floor optional floor identifier
     * @return ResponseEntity with list of RoomMatrixResponse
     */
    @GetMapping("/rooms-matrix")
    public ResponseEntity<List<RoomMatrixResponse>> getRoomStatusMatrix(
            @RequestParam(required = false) String floor) {
        String logFloor = floor != null ? floor : "default";
        log.info("GET request: Fetching room status matrix for floor: {}", logFloor);

        try {
            List<RoomMatrixResponse> roomMatrix = roomService.getRoomStatusMatrixByFloor(floor);
            log.info("Successfully retrieved {} rooms", roomMatrix.size());
            return ResponseEntity.ok(roomMatrix);
        } catch (Exception e) {
            log.error("Error fetching room status matrix for floor: {}", logFloor, e);
            throw e;
        }
    }
}
