package com.product.hms.api;

import com.product.hms.dto.request.ShiftRequest;
import com.product.hms.dto.response.ShiftResponse;
import com.product.hms.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/shifts")
public class AdminShiftController {

    private final ShiftService shiftService;

    public AdminShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    public ResponseEntity<ShiftResponse> createShift(@Valid @RequestBody ShiftRequest request) {
        ShiftResponse created = shiftService.createShift(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftResponse> updateShift(
            @PathVariable Long id,
            @Valid @RequestBody ShiftRequest request) {
        ShiftResponse updated = shiftService.updateShift(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftResponse> getShiftById(@PathVariable Long id) {
        ShiftResponse shift = shiftService.getShiftById(id);
        return ResponseEntity.ok(shift);
    }

    @GetMapping
    public ResponseEntity<List<ShiftResponse>> getAllShifts(
            @RequestParam(value = "activeOnly", defaultValue = "false") Boolean activeOnly) {
        List<ShiftResponse> shifts;
        if (activeOnly) {
            shifts = shiftService.getAllActiveShifts();
        } else {
            shifts = shiftService.getAllShifts();
        }
        return ResponseEntity.ok(shifts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);
        return ResponseEntity.noContent().build();
    }
}
