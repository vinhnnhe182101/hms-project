package com.product.hms.dto.response;

import lombok.Data;

@Data
public class ActiveAllocationResponseDTO {
    private Long allocationId;
    private Long reservationId;
    private String roomNumber;
    private String roomClassName;
}
