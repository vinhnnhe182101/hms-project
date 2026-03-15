package com.product.hms.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardStatResponse {
    private long totalRooms;
    private long occupiedRooms;
    private long dirtyRooms;
    private long checkInsToday;
    private long checkOutsToday;
    private BigDecimal revenueToday;
    private long totalGuests;
    private long pendingReservations;
}
