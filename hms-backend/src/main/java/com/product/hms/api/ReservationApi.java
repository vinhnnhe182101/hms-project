package com.product.hms.api;

import com.product.hms.dto.request.ReservationCheckInRequest;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.request.ReservationSearchFilter;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * Check in a reservation.
     */
    @PostMapping("/{reservationId}/check-in")
    public ResponseEntity<ReservationResponse> checkInReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationCheckInRequest request
    ) {
        ReservationResponse response = reservationService.checkInReservation(reservationId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Search reservations with filter and pagination.
     *
     * @param filter   filter DTO (guestName, status, checkInDateFrom, checkInDateTo)
     * @param pageable Spring Data pageable
     * @return paged result
     */
    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> searchReservations(
            ReservationSearchFilter filter,
            Pageable pageable
    ) {
        Page<ReservationResponse> result = reservationService.search(filter, pageable);
        return ResponseEntity.ok(result);
    }
}
