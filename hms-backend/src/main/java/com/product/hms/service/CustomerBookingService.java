// service/customer/CustomerBookingService.java
package com.product.hms.service;

import com.product.hms.dto.response.BookingDetailResponse;
import com.product.hms.dto.response.BookingHistoryResponse;
import java.util.List;

public interface CustomerBookingService {
    List<BookingHistoryResponse> getBookingHistory();
    BookingDetailResponse getBookingDetails(Long bookingId);
    List<BookingHistoryResponse> getUpcomingBookings();
    void cancelBooking(Long bookingId, String reason);
}