// controller/customer/CustomerReviewController.java
package com.product.hms.api;

import com.product.hms.dto.request.ReviewRequest;
import com.product.hms.dto.request.UpdateReviewRequest;
import com.product.hms.dto.response.ApiResponse;
import com.product.hms.dto.response.ReviewResponse;
import com.product.hms.service.CustomerReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/customer/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerReviewController {

    private final CustomerReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> submitReview(
            @Valid @RequestBody ReviewRequest request) {
        log.info("REST request to submit review for booking: {}", request.getBookingId());
        ReviewResponse response = reviewService.submitReview(request);
        return ResponseEntity.ok(
                ApiResponse.success("Review submitted successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyReviews() {
        log.info("REST request to get customer reviews");
        List<ReviewResponse> reviews = reviewService.getMyReviews();
        return ResponseEntity.ok(
                ApiResponse.success("Reviews retrieved successfully", reviews)
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<Boolean>> hasReviewed(@PathVariable Long bookingId) {
        log.info("REST request to check if booking {} has review", bookingId);
        boolean hasReviewed = reviewService.hasReviewed(bookingId);
        return ResponseEntity.ok(
                ApiResponse.success("Check completed", hasReviewed)
        );
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        log.info("REST request to update review: {}", reviewId);
        ReviewResponse response = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Review updated successfully", response)
        );
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        log.info("REST request to delete review: {}", reviewId);
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(
                ApiResponse.success("Review deleted successfully", null)
        );
    }
}