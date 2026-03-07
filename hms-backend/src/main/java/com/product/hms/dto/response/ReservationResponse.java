package com.product.hms.dto.response;

import java.sql.Timestamp;
import java.util.List;

/**
 * Response DTO for reservation information.
 *
 * @param bookingId       Reservation ID
 * @param bookingCode     Unique reservation code
 * @param customer        Full customer information (avoid extra fetch on frontend)
 * @param allocations     List of room class allocations with IDs (not merged, each entry is separate)
 * @param checkInDate     Expected check-in date
 * @param checkOutDate    Expected check-out date
 * @param status          Reservation status (CONFIRMED, CHECKED_IN, etc.)
 * @param numberOfMembers Total number of guests
 * @param note            Additional notes for the reservation
 * @param createdAt       Timestamp when reservation was created
 */
public record ReservationResponse(
        Long bookingId,
        String bookingCode,
        CustomerResponse customer,
        List<RoomClassQuantityResponse> allocations,
        Timestamp checkInDate,
        Timestamp checkOutDate,
        String status,
        Integer numberOfMembers,
        String note,
        Timestamp createdAt
) {
}

