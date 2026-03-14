// dto/response/housekeeping/MinibarItemResponse.java
package com.product.hms.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MinibarItemResponse {
    private Long id;
    private String assetName;
    private String categoryName;
    private Integer currentQuantity;
    private BigDecimal price;
    private String status;

    public String getPriceDisplay() {
        return String.format("$%.2f", price);
    }
}