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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/room-classes")
@RequiredArgsConstructor
public class RoomClassApi {

    private final RoomClassService roomClassService;

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