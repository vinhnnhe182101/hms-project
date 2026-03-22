package com.product.hms.api;

import com.product.hms.dto.request.UpsertRoomClassRequest;
import com.product.hms.dto.response.RoomClassResponse;
import com.product.hms.service.RoomClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room-classes")
@RequiredArgsConstructor
public class RoomClassApi {

    private final RoomClassService roomClassService;

    @GetMapping("/all")
    public ResponseEntity<List<RoomClassResponse>> getAllRoomClasses() {
        return ResponseEntity.ok(roomClassService.getAllRoomClasses());
    }

    @GetMapping
    public ResponseEntity<Page<RoomClassResponse>> getRoomClasses(Pageable pageable) {
        Pageable sanitizedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(roomClassService.getRoomClassesForAdmin(sanitizedPageable));
    }

    @PostMapping
    public ResponseEntity<RoomClassResponse> createRoomClass(@Valid @RequestBody UpsertRoomClassRequest request) {
        return ResponseEntity.ok(roomClassService.createRoomClass(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomClassResponse> updateRoomClass(
            @PathVariable Long id,
            @Valid @RequestBody UpsertRoomClassRequest request
    ) {
        return ResponseEntity.ok(roomClassService.updateRoomClass(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomClass(@PathVariable Long id) {
        roomClassService.deleteRoomClass(id);
        return ResponseEntity.noContent().build();
    }
}