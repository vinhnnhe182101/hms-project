package com.product.hms.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ServiceBookingRequestDTO {
    private Long customerId;
    private List<ServiceItem> items;

    @Data
    public static class ServiceItem {
        private Long serviceId;
        private Long allocationId;
        private Integer quantity;
        private BigDecimal price;
    }
}
