package com.product.hms.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RatingSummaryResponse {
    private Double averageRating;
    private Long totalReviews;
    private Map<Integer, Long> ratingDistribution;

    private List<RatingResponse> content;
}
