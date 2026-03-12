package com.product.hms.api;

import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.request.RoomChangeRequest;
import com.product.hms.dto.response.PaymentResponse;
import com.product.hms.dto.response.ReservationRoomCheckOutResponse;
import com.product.hms.dto.response.ReservationRoomFolioResponse;
import com.product.hms.service.ReservationRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for reservation room operations (check-out, folio preview)
 */
@RestController
@RequestMapping("/api/v1/reservation-rooms")
@RequiredArgsConstructor
public class ReservationRoomApi {

    private final ReservationRoomService reservationRoomService;

    /**
     * Get folio details for a reservation room (for checkout preview).
     * Returns: room info, occupants, folio items, balance.
     */
    @GetMapping("/{reservationRoomId}/folio")
    public ResponseEntity<ReservationRoomFolioResponse> getReservationRoomFolio(
            @PathVariable Long reservationRoomId
    ) {
        ReservationRoomFolioResponse response = reservationRoomService.getReservationRoomFolio(reservationRoomId);
        return ResponseEntity.ok(response);
    }

    /**
     * Check out a specific reservation room.
     * Applies late check-out fee if applicable.
     */
    @PostMapping("/{reservationRoomId}/check-out")
    public ResponseEntity<ReservationRoomCheckOutResponse> checkOutReservationRoom(
            @PathVariable Long reservationRoomId
    ) {
        ReservationRoomCheckOutResponse response = reservationRoomService.checkOutReservationRoom(reservationRoomId);
        return ResponseEntity.ok(response);
    }

    /**
     * Process payment for a reservation room.
     * Creates payment transaction and updates folio balance.
     */
    @PostMapping("/{reservationRoomId}/payment")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long reservationRoomId,
            @RequestBody PaymentRequest request
    ) {
        PaymentResponse response = reservationRoomService.processPayment(reservationRoomId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Chuyển phòng cho khách đã check-in.
     * @param reservationRoomId ID của reservation room cần chuyển phòng
     * @param request Thông tin chuyển phòng
     */
    @PostMapping("/{reservationRoomId}/room-change")
    public ResponseEntity<Void> changeRoom(
            @PathVariable Long reservationRoomId,
            @RequestBody RoomChangeRequest request
    ) {
        reservationRoomService.changeRoom(reservationRoomId, request);
        return ResponseEntity.ok().build();
    }
}
