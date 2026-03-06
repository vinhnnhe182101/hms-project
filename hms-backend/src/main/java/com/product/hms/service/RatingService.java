package com.product.hms.service;

import com.product.hms.dto.response.RatingSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RatingService {
    Page<RatingSummaryResponse> getRatingsByRoomClass(Long roomClassId, Integer ratingFilter, Pageable pageable);
}
