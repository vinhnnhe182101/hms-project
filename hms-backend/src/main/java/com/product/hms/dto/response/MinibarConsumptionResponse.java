// dto/response/housekeeping/MinibarConsumptionResponse.java
package com.product.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Builder
public class MinibarConsumptionResponse {
    private Long id;
    private String roomNumber;
    private String assetName;
    private Integer quantity;
    private BigDecimal price;
    private Double total;
    private String status;
    private Timestamp createdAt;

    public String getFormattedTotal() {
        return String.format("$%.2f", total);
    }
}