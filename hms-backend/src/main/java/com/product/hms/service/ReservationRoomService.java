package com.product.hms.service;

import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.response.PaymentResponse;
import com.product.hms.dto.response.ReservationRoomCheckOutResponse;
import com.product.hms.dto.response.ReservationRoomFolioResponse;

/**
 * Service interface for reservation room operations (check-out, folio management, payment)
 */
public interface ReservationRoomService {

    /**
     * Get folio details for a reservation room (for checkout preview).
     * Includes: room info, occupants, folio items, balance.
     *
     * @param reservationRoomId reservation room id
     * @return ReservationRoomFolioResponse with all folio details
     */
    ReservationRoomFolioResponse getReservationRoomFolio(Long reservationRoomId);

    /**
     * Check out a specific reservation room.
     * Applies late check-out fee if applicable.
     * Updates room status to CHECKED_OUT and physical room to DIRTY.
     * Updates reservation status to CHECKED_OUT if all rooms are checked out.
     *
     * @param reservationRoomId reservation room id to check out
     * @return ReservationRoomCheckOutResponse with status
     */
    ReservationRoomCheckOutResponse checkOutReservationRoom(Long reservationRoomId);

    /**
     * Process payment for selected folio items of a reservation room.
     * Backend computes payable amount from selected folio items and applies deposit deduction if requested.
     * For VNPAY, response includes a redirect URL generated after creating payment transaction/allocation.
     *
     * @param reservationRoomId reservation room id
     * @param request           payment details (selected folio item ids, method, deposit to deduct, vnpay redirect inputs)
     * @return payment breakdown, remaining folio balance, and optional VNPAY redirect URL
     */
    PaymentResponse processPayment(Long reservationRoomId, PaymentRequest request);
}
