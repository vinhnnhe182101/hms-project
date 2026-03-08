package com.product.hms.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.hms.advice.GlobalExceptionHandler;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ReservationApi Integration Tests")
class ReservationApiIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = mock(ReservationService.class);
        ReservationApi reservationApi = new ReservationApi(reservationService);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationApi)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("PUT /api/v1/reservations/{id} should return updated reservation")
    void updateReservation_ShouldReturnUpdatedReservation() throws Exception {
        Long reservationId = 10L;
        Timestamp checkIn = Timestamp.from(Instant.now().plus(3, ChronoUnit.DAYS));
        Timestamp checkOut = Timestamp.from(Instant.now().plus(5, ChronoUnit.DAYS));

        ReservationResponse response = new ReservationResponse(
                reservationId,
                "RS260307ZZZZZZ",
                new com.product.hms.dto.response.CustomerResponse(2L, "Nguyen Van A", "0900000000", "012345678901", "a@example.com", "ADULT"),
                List.of(new com.product.hms.dto.response.RoomClassQuantityResponse(100L, 1L, 3)),
                checkIn,
                checkOut,
                "CONFIRMED",
                3,
                "Updated note",
                Timestamp.from(Instant.now())
        );

        when(reservationService.updateReservation(eq(reservationId), any())).thenReturn(response);

        String requestJson = """
                {
                  "customerRequest": {
                    "customerId": 2
                  },
                  "roomClassQuantities": [
                    {
                      "roomClassId": 1,
                      "numberOfPeople": 3
                    }
                  ],
                  "checkInDate": %d,
                  "checkOutDate": %d,
                  "numberOfMembers": 3,
                  "note": "Updated note"
                }
                """.formatted(checkIn.getTime(), checkOut.getTime());

        mockMvc.perform(put("/api/v1/reservations/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(10L))
                .andExpect(jsonPath("$.bookingCode").value("RS260307ZZZZZZ"))
                .andExpect(jsonPath("$.numberOfMembers").value(3))
                .andExpect(jsonPath("$.note").value("Updated note"))
                .andExpect(jsonPath("$.allocations[0].roomClassId").value(1L))
                .andExpect(jsonPath("$.allocations[0].numberOfPeople").value(3));
    }

    @Test
    @DisplayName("PUT /api/v1/reservations/{id} should map business error via advice")
    void updateReservation_ShouldMapBusinessException() throws Exception {
        Long reservationId = 10L;
        when(reservationService.updateReservation(eq(reservationId), any()))
                .thenThrow(new BusinessException(
                        ErrorCode.RESERVATION_UPDATE_LOCKED,
                        "Reservation cannot be updated within 24 hours before check-in"
                ));

        String requestJson = objectMapper.writeValueAsString(new HashMap<>());

        mockMvc.perform(put("/api/v1/reservations/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESERVATION_UPDATE_LOCKED"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.path").value("/api/v1/reservations/10"));
    }

    @Test
    @DisplayName("PUT /api/v1/reservations/{id} should map not found error via advice")
    void updateReservation_ShouldMapNotFoundException() throws Exception {
        Long reservationId = 404L;
        when(reservationService.updateReservation(eq(reservationId), any()))
                .thenThrow(new NotFoundException(ErrorCode.RESERVATION_NOT_FOUND, "Reservation not found with ID: 404"));

        String requestJson = objectMapper.writeValueAsString(new HashMap<>());

        mockMvc.perform(put("/api/v1/reservations/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESERVATION_NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE /api/v1/reservations/{id} should cancel reservation and return CANCELLED status")
    void cancelReservation_ShouldReturnCancelledReservation() throws Exception {
        Long reservationId = 10L;
        Timestamp checkIn = Timestamp.from(Instant.now().plus(3, ChronoUnit.DAYS));
        Timestamp checkOut = Timestamp.from(Instant.now().plus(5, ChronoUnit.DAYS));

        ReservationResponse response = new ReservationResponse(
                reservationId,
                "RS260307XYZ456",
                new com.product.hms.dto.response.CustomerResponse(2L, "Nguyen Van A", "0900000000", "012345678901", "a@example.com", "ADULT"),
                List.of(new com.product.hms.dto.response.RoomClassQuantityResponse(100L, 1L, 2)),
                checkIn,
                checkOut,
                "CANCELLED",
                2,
                "Original note",
                Timestamp.from(Instant.now())
        );

        when(reservationService.cancelReservation(reservationId)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/reservations/{reservationId}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(10L))
                .andExpect(jsonPath("$.bookingCode").value("RS260307XYZ456"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("DELETE /api/v1/reservations/{id} should return 404 when reservation not found")
    void cancelReservation_NotFound_ShouldReturn404() throws Exception {
        Long reservationId = 999L;
        when(reservationService.cancelReservation(reservationId))
                .thenThrow(new NotFoundException(ErrorCode.RESERVATION_NOT_FOUND, "Reservation not found"));

        mockMvc.perform(delete("/api/v1/reservations/{reservationId}", reservationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESERVATION_NOT_FOUND"));
    }

    @Test
    @DisplayName("DELETE /api/v1/reservations/{id} should return 409 when reservation already canceled")
    void cancelReservation_AlreadyCanceled_ShouldReturn409() throws Exception {
        Long reservationId = 10L;
        when(reservationService.cancelReservation(reservationId))
                .thenThrow(new BusinessException(ErrorCode.RESERVATION_ALREADY_CANCELED, "Reservation is already canceled"));

        mockMvc.perform(delete("/api/v1/reservations/{reservationId}", reservationId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESERVATION_ALREADY_CANCELED"))
                .andExpect(jsonPath("$.status").value(409));
    }
}
