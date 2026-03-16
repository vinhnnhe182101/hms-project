package com.product.hms.service;

import com.product.hms.dto.request.RejectRefundRequest;
import com.product.hms.dto.response.RefundRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RefundRequestService {
    Page<RefundRequestResponse> getPendingRefundRequests(Pageable pageable);
    RefundRequestResponse getRefundRequestById(Long id);
    RefundRequestResponse rejectRefundRequest(Long id, Long adminStaffId, RejectRefundRequest request);
    RefundRequestResponse approveRefundRequest(Long id, Long adminStaffId, String clientIp);
}
