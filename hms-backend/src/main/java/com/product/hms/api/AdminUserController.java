package com.product.hms.api;

import com.product.hms.dto.response.UserResponseDTO;
import com.product.hms.enums.Role;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String roleStr = body.get("role");
        if (roleStr == null || roleStr.isBlank()) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Role is required");
        }
        try {
            Role role = Role.valueOf(roleStr);
            return ResponseEntity.ok(userService.updateUserRole(id, role));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Invalid role: " + roleStr);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        Boolean isActive = body.get("isActive");
        if (isActive == null) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "isActive is required");
        }
        return ResponseEntity.ok(userService.updateUserStatus(id, isActive));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}