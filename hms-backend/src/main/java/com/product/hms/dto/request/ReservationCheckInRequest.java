package com.product.hms.dto.request;

import java.util.List;

/**
 * Request DTO for reservation check-in.
 *
 * @param autoAssign      if true, system auto-assigns rooms for allocations not provided in roomAssignments
 * @param roomAssignments manual assignment list (can be partial when autoAssign=true)
 */
public record ReservationCheckInRequest(
        Boolean autoAssign,
        List<ReservationRoomCheckInRequest> roomAssignments
) {
}

