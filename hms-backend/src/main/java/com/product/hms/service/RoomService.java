package com.product.hms.service;

import com.product.hms.dto.response.RoomClassAvailabilityResponse;
import com.product.hms.dto.response.RoomClassAvailableRoomsResponse;

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
}
