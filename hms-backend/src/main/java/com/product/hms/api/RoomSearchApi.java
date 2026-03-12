package com.product.hms.api;

import com.product.hms.dto.request.RoomSearchFilter;
import com.product.hms.dto.response.RoomClassAvailableRoomsResponse;
import com.product.hms.service.RoomSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms/search")
@RequiredArgsConstructor
public class RoomSearchApi {
    private final RoomSearchService roomSearchService;

    @GetMapping
    public ResponseEntity<Page<RoomClassAvailableRoomsResponse>> search(
            RoomSearchFilter filter,
            Pageable pageable
    ) {
        Page<RoomClassAvailableRoomsResponse> result = roomSearchService.search(filter, pageable);
        return ResponseEntity.ok(result);
    }
}

