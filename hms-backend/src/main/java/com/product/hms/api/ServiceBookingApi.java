package com.product.hms.api;

import com.product.hms.dto.request.ServiceBookingRequest;
import com.product.hms.dto.request.UpdateServiceBookingRequest;
import com.product.hms.dto.response.ServiceBookingResponse;
import com.product.hms.service.ServiceBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for service booking operations
 */
@RestController
@RequestMapping("/api/v1/reservation-rooms")
@RequiredArgsConstructor
public class ServiceBookingApi {

    private final ServiceBookingService serviceBookingService;

    /**
     * Create a new service booking for a reservation room.
     * Only allowed when reservation status is CHECKED_IN.
     *
     * @param reservationRoomId reservation room id
     * @param request           service booking request
     * @return ResponseEntity containing created service booking
     */
    @PostMapping("/{reservationRoomId}/services")
    public ResponseEntity<ServiceBookingResponse> createServiceBooking(
            @PathVariable Long reservationRoomId,
            @RequestBody ServiceBookingRequest request
    ) {
        ServiceBookingResponse response = serviceBookingService.createServiceBooking(reservationRoomId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update a service booking (only allowed when status is PENDING).
     * Only quantity can be changed.
     *
     * @param reservationRoomId reservation room id
     * @param serviceBookingId  service booking id
     * @param request           update payload (quantity only)
     * @return ResponseEntity with updated service booking
     */
    @PutMapping("/{reservationRoomId}/services/{serviceBookingId}")
    public ResponseEntity<ServiceBookingResponse> updateServiceBooking(
            @PathVariable Long reservationRoomId,
            @PathVariable Long serviceBookingId,
            @RequestBody UpdateServiceBookingRequest request
    ) {
        ServiceBookingResponse response = serviceBookingService.updateServiceBooking(reservationRoomId, serviceBookingId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a service booking (only allowed when status is PENDING).
     *
     * @param reservationRoomId reservation room id
     * @param serviceBookingId  service booking id
     * @return ResponseEntity with canceled service booking
     */
    @DeleteMapping("/{reservationRoomId}/services/{serviceBookingId}")
    public ResponseEntity<ServiceBookingResponse> cancelServiceBooking(
            @PathVariable Long reservationRoomId,
            @PathVariable Long serviceBookingId
    ) {
        ServiceBookingResponse response = serviceBookingService.cancelServiceBooking(reservationRoomId, serviceBookingId);
        return ResponseEntity.ok(response);
    }
}
