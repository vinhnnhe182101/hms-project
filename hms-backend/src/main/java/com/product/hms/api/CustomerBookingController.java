// controller/customer/CustomerBookingController.java
package com.product.hms.api;

import com.product.hms.dto.request.CancelBookingRequest;
import com.product.hms.dto.response.ApiResponse;
import com.product.hms.dto.response.BookingDetailResponse;
import com.product.hms.dto.response.BookingHistoryResponse;
import com.product.hms.service.CustomerBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customer/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerBookingController {

    private final CustomerBookingService bookingService;

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<BookingHistoryResponse>>> getBookingHistory() {
        log.info("REST request to get booking history");
        List<BookingHistoryResponse> bookings = bookingService.getBookingHistory();
        return ResponseEntity.ok(
                ApiResponse.success("Booking history retrieved successfully", bookings)
        );
    }

    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<BookingHistoryResponse>>> getUpcomingBookings() {
        log.info("REST request to get upcoming bookings");
        List<BookingHistoryResponse> bookings = bookingService.getUpcomingBookings();
        return ResponseEntity.ok(
                ApiResponse.success("Upcoming bookings retrieved successfully", bookings)
        );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDetailResponse>> getBookingDetails(
            @PathVariable Long bookingId) {
        log.info("REST request to get booking details for id: {}", bookingId);
        BookingDetailResponse booking = bookingService.getBookingDetails(bookingId);
        return ResponseEntity.ok(
                ApiResponse.success("Booking details retrieved successfully", booking)
        );
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody CancelBookingRequest request) {
        log.info("REST request to cancel booking id: {} with reason: {}", bookingId, request.getReason());
        bookingService.cancelBooking(bookingId, request.getReason());
        return ResponseEntity.ok(
                ApiResponse.success("Booking cancelled successfully", null)
        );
    }
}