package com.product.hms.service;

import com.product.hms.dto.response.RoomClassAvailabilityResponse;
import com.product.hms.dto.response.RoomClassAvailableRoomsResponse;
import com.product.hms.dto.response.RoomMatrixResponse;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service interface for room operations
 */
public interface RoomService {

    /**
     * Get available rooms by room class for a given date range
     *
     * @param checkInDate  the check-in date
     * @param checkOutDate the check-out date
     * @return List of RoomClassAvailabilityResponse containing room class info and available room counts
     */
    List<RoomClassAvailabilityResponse> getAvailableRooms(Timestamp checkInDate, Timestamp checkOutDate);

    /**
     * Get available physical rooms grouped by room class for manual assignment.
     */
    List<RoomClassAvailableRoomsResponse> getAvailableRoomsForAssignment(Timestamp checkInDate, Timestamp checkOutDate);

    /**
     * Get available physical rooms for one room class.
     */
    List<com.product.hms.dto.response.AvailableRoomResponse> getAvailableRoomsByRoomClassIdForAssignment(
            Long roomClassId,
            Timestamp checkInDate,
            Timestamp checkOutDate
    );

    /**
     * Get all available floors from active rooms.
     * Floors are extracted from room numbers and sorted numerically.
     *
     * @return List of floor strings sorted numerically (e.g., ["1", "2", "3", "12"])
     */
    List<String> getAvailableFloors();

    /**
     * Get room status matrix for a specific floor.
     * Rooms are sorted by room number in ascending order.
     *
     * @param floor the floor identifier (e.g., "1", "12"). If null or blank, uses first available floor.
     * @return List of RoomMatrixResponse containing room information for the specified floor
     */
    List<RoomMatrixResponse> getRoomStatusMatrixByFloor(String floor);
}
