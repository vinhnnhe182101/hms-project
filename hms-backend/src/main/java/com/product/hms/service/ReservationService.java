package com.product.hms.service;

import com.product.hms.dto.request.ReservationCheckInRequest;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.response.ReservationResponse;

/**
 * Service interface for reservation operations
 */
public interface ReservationService {

    /**
     * Create a new reservation
     *
     * @param request the reservation request containing customer and room information
     * @return ReservationResponse containing the created reservation information
     */
    ReservationResponse createReservation(ReservationRequest request);

    /**
     * Update an existing reservation.
     *
     * @param reservationId reservation id
     * @param request       update payload
     * @return ReservationResponse containing updated reservation information
     */
    ReservationResponse updateReservation(Long reservationId, ReservationRequest request);

    /**
     * Cancel an existing reservation.
     * Applies cancellation policy: refund deposit if canceled >24h before check-in.
     *
     * @param reservationId reservation id to cancel
     * @return ReservationResponse with CANCELED status
     */
    ReservationResponse cancelReservation(Long reservationId);

    /**
     * Check in a reservation and assign physical rooms for each reservation room.
     *
     * @param reservationId reservation id
     * @param request       check-in payload (manual assignment and/or auto assign)
     * @return updated reservation response in IN_HOUSE status
     */
    ReservationResponse checkInReservation(Long reservationId, ReservationCheckInRequest request);
}
