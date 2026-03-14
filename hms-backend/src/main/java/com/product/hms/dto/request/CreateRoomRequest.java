package com.product.hms.dto.request;

import com.product.hms.enums.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRoomRequest(
        @NotBlank(message = "Room number is required")
        @Size(max = 50, message = "Room number must be at most 50 characters")
        String roomNumber,

        @NotNull(message = "Room class is required")
        Long roomClassId,

        @NotNull(message = "Room status is required")
        RoomStatus status,

        String description
) {
}
