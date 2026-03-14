// controller/housekeeping/DamageController.java
package com.product.hms.api.housekeeping;

import com.product.hms.dto.request.DamageReportRequest;
import com.product.hms.dto.response.ApiResponse;
import com.product.hms.dto.response.DamageReportResponse;
import com.product.hms.service.housekeeping.DamageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/housekeeping/damage")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('HOUSEKEEPING')")
public class DamageController {

    private final DamageService damageService;

    @PostMapping("/report")
    public ResponseEntity<ApiResponse<DamageReportResponse>> reportDamage(
            @Valid @RequestBody DamageReportRequest request) {
        DamageReportResponse response = damageService.reportDamage(request);
        return ResponseEntity.ok(
                ApiResponse.success("Damage reported successfully", response)
        );
    }

    @GetMapping("/my-reports")
    public ResponseEntity<ApiResponse<List<DamageReportResponse>>> getMyDamageReports() {
        List<DamageReportResponse> reports = damageService.getMyDamageReports();
        return ResponseEntity.ok(
                ApiResponse.success("Damage reports retrieved successfully", reports)
        );
    }

    @PostMapping("/{reportId}/resolve")
    public ResponseEntity<ApiResponse<DamageReportResponse>> resolveDamage(
            @PathVariable Long reportId) {
        DamageReportResponse response = damageService.resolveDamage(reportId);
        return ResponseEntity.ok(
                ApiResponse.success("Damage report resolved successfully", response)
        );
    }
}