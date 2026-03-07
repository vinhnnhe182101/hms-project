package com.product.hms.api.controller.admin;

import com.product.hms.dto.RoomTypeCreateDTO;
import com.product.hms.dto.RoomTypeResponseDTO;
import com.product.hms.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/room-types")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomTypeController {

    private final RoomTypeService roomTypeService;

    @PostMapping
    public ResponseEntity<RoomTypeResponseDTO> create(
            @Valid @RequestBody RoomTypeCreateDTO dto) {
        return ResponseEntity.ok(roomTypeService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RoomTypeCreateDTO dto) {
        return ResponseEntity.ok(roomTypeService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roomTypeService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<RoomTypeResponseDTO>> getAllActive() {
        return ResponseEntity.ok(roomTypeService.findAllActive());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}