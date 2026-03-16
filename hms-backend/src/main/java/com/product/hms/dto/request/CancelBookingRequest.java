// dto/request/CancelBookingRequest.java
package com.product.hms.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelBookingRequest {
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}