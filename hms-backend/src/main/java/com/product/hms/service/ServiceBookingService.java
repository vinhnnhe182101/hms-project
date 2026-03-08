package com.product.hms.service;

import com.product.hms.dto.request.ServiceBookingRequestDTO;
import com.product.hms.dto.response.ActiveAllocationResponseDTO;

import java.util.List;

public interface ServiceBookingService {
    List<ActiveAllocationResponseDTO> getActiveAllocationsByCustomer(Long customerId);
    void createServiceBookings(ServiceBookingRequestDTO request);
}
