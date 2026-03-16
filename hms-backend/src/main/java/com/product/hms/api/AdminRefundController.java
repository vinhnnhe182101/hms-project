package com.product.hms.api;

import com.product.hms.dto.request.RejectRefundRequest;
import com.product.hms.dto.response.RefundRequestResponse;
import com.product.hms.service.RefundRequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/refunds")
@RequiredArgsConstructor
public class AdminRefundController {

    private final RefundRequestService refundRequestService;

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRefundRequests(Pageable pageable) {
        return ResponseEntity.ok(refundRequestService.getPendingRefundRequests(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefundRequestResponse> getRefundRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(refundRequestService.getRefundRequestById(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectRefundRequest(
            @PathVariable Long id,
            @RequestBody @Valid RejectRefundRequest request
    ) {
        Long adminStaffId = 1L;
        return ResponseEntity.ok(refundRequestService.rejectRefundRequest(id, adminStaffId, request));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveRefundRequest(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        Long adminStaffId = 1L;
        String clientIp = httpRequest.getRemoteAddr();
        return ResponseEntity.ok(refundRequestService.approveRefundRequest(id, adminStaffId, clientIp));
    }
}
