package com.product.hms.service;

import com.product.hms.dto.response.RoomClassAvailabilityResponse;

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
}

