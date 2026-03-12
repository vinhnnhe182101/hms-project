package com.product.hms.api;

import com.product.hms.dto.request.ReservationSearchFilter;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.service.ReservationSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations/search")
@RequiredArgsConstructor
public class ReservationSearchApi {
    private final ReservationSearchService reservationSearchService;

    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> search(
            ReservationSearchFilter filter,
            Pageable pageable
    ) {
        Page<ReservationResponse> result = reservationSearchService.search(filter, pageable);
        return ResponseEntity.ok(result);
    }
}

