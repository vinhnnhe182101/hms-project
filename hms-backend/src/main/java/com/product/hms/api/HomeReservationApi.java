package com.product.hms.api;

import com.product.hms.dto.request.BookingRequestDTO;
import com.product.hms.dto.response.BookingResponseDTO;
import com.product.hms.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/home/reservations")
@RequiredArgsConstructor
public class HomeReservationApi {

    private final ReservationService reservationService;

    @PostMapping("/booking")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO request) {
        try {
            BookingResponseDTO response = reservationService.createBooking(request);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown Error"));
        }
    }
}
