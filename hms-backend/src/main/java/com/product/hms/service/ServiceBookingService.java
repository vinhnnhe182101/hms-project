package com.product.hms.service;

import com.product.hms.dto.request.ServiceBookingRequest;
import com.product.hms.dto.request.UpdateServiceBookingRequest;
import com.product.hms.dto.response.ServiceBookingResponse;

/**
 * Service interface for service booking operations
 */
public interface ServiceBookingService {

    /**
     * Create a new service booking for a reservation room.
     * Only allowed when reservation status is CHECKED_IN.
     *
     * @param reservationRoomId reservation room id
     * @param request           service booking request
     * @return ServiceBookingResponse containing the created service booking information
     */
    ServiceBookingResponse createServiceBooking(Long reservationRoomId, ServiceBookingRequest request);

    /**
     * Update a service booking (only allowed when status is PENDING).
     * Only quantity can be changed - to change service, create a new booking.
     *
     * @param reservationRoomId reservation room id
     * @param serviceBookingId  service booking id
     * @param request           update payload (quantity only)
     * @return updated service booking
     */
    ServiceBookingResponse updateServiceBooking(Long reservationRoomId, Long serviceBookingId, UpdateServiceBookingRequest request);

    /**
     * Cancel a service booking (only allowed when status is PENDING).
     *
     * @param reservationRoomId reservation room id
     * @param serviceBookingId  service booking id
     * @return canceled service booking
     */
    ServiceBookingResponse cancelServiceBooking(Long reservationRoomId, Long serviceBookingId);
}
