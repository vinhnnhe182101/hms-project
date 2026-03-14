// controller/housekeeping/MinibarController.java
package com.product.hms.api.housekeeping;

import com.product.hms.dto.request.MinibarConsumptionRequest;
import com.product.hms.dto.response.ApiResponse;
import com.product.hms.dto.response.MinibarConsumptionResponse;
import com.product.hms.dto.response.MinibarItemResponse;
import com.product.hms.service.housekeeping.MinibarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/housekeeping/minibar")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HOUSEKEEPING')")
public class MinibarController {

    private final MinibarService minibarService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ApiResponse<List<MinibarItemResponse>>> getRoomMinibarItems(
            @PathVariable Long roomId) {
        List<MinibarItemResponse> items = minibarService.getRoomMinibarItems(roomId);
        return ResponseEntity.ok(
                ApiResponse.success("Minibar items retrieved successfully", items)
        );
    }

    @PostMapping("/consume")
    public ResponseEntity<ApiResponse<List<MinibarConsumptionResponse>>> reportConsumption(
            @Valid @RequestBody MinibarConsumptionRequest request) {
        List<MinibarConsumptionResponse> responses = minibarService.reportConsumption(request);
        return ResponseEntity.ok(
                ApiResponse.success("Minibar consumption recorded successfully", responses)
        );
    }

    @GetMapping("/history/{reservationId}")
    public ResponseEntity<ApiResponse<List<MinibarConsumptionResponse>>> getConsumptionHistory(
            @PathVariable Long reservationId) {
        List<MinibarConsumptionResponse> history = minibarService.getConsumptionHistory(reservationId);
        return ResponseEntity.ok(
                ApiResponse.success("Consumption history retrieved successfully", history)
        );
    }
}