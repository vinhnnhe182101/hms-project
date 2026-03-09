package com.product.hms.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for reservation room folio details (for checkout preview)
 *
 * @param reservationRoomId Reservation room ID
 * @param roomNumber        Physical room number
 * @param roomClassName     Room class name
 * @param occupants         List of room occupants
 * @param folioItems        List of folio items (charges)
 * @param totalCharges      Total charges amount
 * @param totalPaid         Total paid amount
 * @param balance           Outstanding balance
 */
public record ReservationRoomFolioResponse(
        Long reservationRoomId,
        String roomNumber,
        String roomClassName,
        List<RoomOccupantResponse> occupants,
        List<FolioItemResponse> folioItems,
        BigDecimal totalCharges,
        BigDecimal totalPaid,
        BigDecimal balance
) {
}

