package com.product.hms.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectRefundRequest {
    @NotNull
    private String rejectReason;
}
