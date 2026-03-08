package com.product.hms.api;

import com.product.hms.dto.request.ServiceBookingRequestDTO;
import com.product.hms.dto.response.ActiveAllocationResponseDTO;
import com.product.hms.service.ServiceBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Hỗ trợ dev frontend local
public class ServiceBookingApi {

    private final ServiceBookingService serviceBookingService;

    @GetMapping("/allocations")
    public ResponseEntity<List<ActiveAllocationResponseDTO>> getActiveAllocations(@RequestParam Long customerId) {
        List<ActiveAllocationResponseDTO> allocations = serviceBookingService.getActiveAllocationsByCustomer(customerId);
        return ResponseEntity.ok(allocations);
    }

    @PostMapping
    public ResponseEntity<String> createServiceBookings(@RequestBody ServiceBookingRequestDTO request) {
        try {
            serviceBookingService.createServiceBookings(request);
            return ResponseEntity.ok("Đặt dịch vụ thành công.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi đặt dịch vụ: " + e.getMessage());
        }
    }
}
