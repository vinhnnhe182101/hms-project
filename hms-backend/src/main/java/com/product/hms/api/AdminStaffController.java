package com.product.hms.api;

import com.product.hms.dto.request.StaffAccountRequestDTO;
import com.product.hms.dto.request.StaffRequestDTO;
import com.product.hms.dto.response.StaffResponseDTO;
import com.product.hms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/staff")
@RequiredArgsConstructor
public class AdminStaffController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<StaffResponseDTO>> getAllStaff() {
        return ResponseEntity.ok(userService.getAllStaff());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<StaffResponseDTO>> getStaffPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive,
            Pageable pageable
    ) {
        Page<StaffResponseDTO> page = userService.getStaffWithPagination(name, email, phoneNumber, department, status, isActive, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> getStaffById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getStaffById(id));
    }

    @PostMapping
    public ResponseEntity<StaffResponseDTO> createStaffAccount(@Valid @RequestBody StaffAccountRequestDTO request) {
        StaffResponseDTO created = userService.createStaffAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody StaffRequestDTO request) {
        StaffResponseDTO updated = userService.updateStaff(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        userService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}