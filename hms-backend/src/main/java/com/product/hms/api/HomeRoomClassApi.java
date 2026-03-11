package com.product.hms.api;

import com.product.hms.dto.response.RoomClassDetailResponse;
import com.product.hms.dto.response.RoomClassResponse;
import com.product.hms.service.RoomClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/v1/home/room-classes")
@RequiredArgsConstructor

public class HomeRoomClassApi {

    private final RoomClassService roomClassService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoomClassList(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        if (checkIn == null) {
            checkIn = LocalDateTime.now();
        }
        if (checkOut == null) {
            checkOut = checkIn.plusDays(1);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<RoomClassResponse> resultPage = roomClassService.getRoomClassList(
                Timestamp.valueOf(checkIn),
                Timestamp.valueOf(checkOut),
                pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("data", resultPage.getContent());
        response.put("currentPage", resultPage.getNumber());
        response.put("totalItems", resultPage.getTotalElements());
        response.put("totalPages", resultPage.getTotalPages());
        response.put("pageSize", resultPage.getSize());
        response.put("isLast", resultPage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomClassDetailResponse> getRoomClassDetail(@PathVariable Long id) {
        return ResponseEntity.ok(roomClassService.getRoomClassDetail(id));
    }


    @GetMapping("/{id}/others")
    public ResponseEntity<List<RoomClassResponse>> getOtherRoomClasses(@PathVariable Long id) {
        return ResponseEntity.ok(roomClassService.getOtherRoomClasses(id));
    }
}
