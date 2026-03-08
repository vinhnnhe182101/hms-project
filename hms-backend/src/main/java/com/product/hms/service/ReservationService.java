package com.product.hms.service;

import com.product.hms.dto.request.BookingRequestDTO;

public interface ReservationService {
    Long createBooking(BookingRequestDTO request);
}
