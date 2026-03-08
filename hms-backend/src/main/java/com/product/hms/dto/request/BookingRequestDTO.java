package com.product.hms.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class BookingRequestDTO {
    private Instant checkIn;
    private Instant checkOut;
    private Integer nights;
    private Integer guests;
    private List<RoomBookingRequest> rooms;
    private CustomerRequest customer;

    @Data
    public static class RoomBookingRequest {
        private Long id; // This is the roomClassId
        private String name;
        private Integer quantity;
        private BigDecimal pricePerNight;
        private BigDecimal total;
    }

    @Data
    public static class CustomerRequest {
        private Long customerId;   // nếu đã đăng nhập, ưu tiên dùng cái này
        private String name;
        private String phone;
        private String identityCard;
        private String note;
    }
}
