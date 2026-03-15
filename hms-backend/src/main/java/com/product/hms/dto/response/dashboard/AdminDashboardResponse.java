package com.product.hms.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class AdminDashboardResponse {
    private DashboardStatResponse stats;
    private List<RecentBookingDto> recentBookings;
}
