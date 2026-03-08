package com.product.hms.api;

import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for reservation CRUD operations
 */
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationApi {

    private final ReservationService reservationService;

    /**
     * Create a new reservation
     *
     * @param request the reservation request
     * @return ResponseEntity containing the created reservation information
     */
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update reservation core information.
     */
    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationRequest request
    ) {
        ReservationResponse response = reservationService.updateReservation(reservationId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a reservation.
     * Applies cancellation policy: full refund if >24h before check-in, otherwise deposit forfeited.
     *
     * @param reservationId reservation id to cancel
     * @return ResponseEntity with canceled reservation details
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long reservationId) {
        ReservationResponse response = reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok(response);
    }
}
