package com.product.hms.dto.request;

import java.sql.Timestamp;
import java.util.List;

/**
 * DTO for reservation operations (create and update)
 *
 * @param customerRequest     Customer payload (existing via customerId, or new customer details)
 * @param roomClassQuantities List of room class quantities (allows duplicate room classes with different numberOfPeople)
 * @param checkInDate         Expected check-in date
 * @param checkOutDate        Expected check-out date
 * @param numberOfMembers     Total number of guests
 * @param note                Additional notes or special requests
 */
public record ReservationRequest(
        CustomerRequest customerRequest,
        List<RoomClassQuantityRequest> roomClassQuantities,
        Timestamp checkInDate,
        Timestamp checkOutDate,
        Integer numberOfMembers,
        String note
) {
}

