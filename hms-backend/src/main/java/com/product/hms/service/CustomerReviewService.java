// service/customer/CustomerReviewService.java
package com.product.hms.service;

import com.product.hms.dto.request.ReviewRequest;
import com.product.hms.dto.request.UpdateReviewRequest;
import com.product.hms.dto.response.ReviewResponse;
import java.util.List;

public interface CustomerReviewService {
    ReviewResponse submitReview(ReviewRequest request);
    List<ReviewResponse> getMyReviews();
    ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request);
    void deleteReview(Long reviewId);
    boolean hasReviewed(Long bookingId);
}