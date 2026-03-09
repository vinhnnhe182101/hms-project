package com.product.hms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Room Status Matrix representation.
 * Used for displaying room information grouped by floor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMatrixResponse {
    private Long id;
    private String roomNumber;
    private String status;
    private String roomClassName;
}
