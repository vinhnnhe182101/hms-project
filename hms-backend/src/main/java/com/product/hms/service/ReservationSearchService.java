package com.product.hms.service;

import com.product.hms.dto.request.ReservationSearchFilter;
import com.product.hms.dto.response.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationSearchService {
    Page<ReservationResponse> search(ReservationSearchFilter filter, Pageable pageable);
}

