package com.product.hms.api;

import com.product.hms.dto.response.RatingSummaryResponse;
import com.product.hms.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingApi {

    private final RatingService ratingService;

    @GetMapping("/room-class/{roomClassId}")
    public ResponseEntity<Map<String, Object>> getRatingsByRoomClass(
            @PathVariable Long roomClassId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<RatingSummaryResponse> feedbackPage = ratingService.getRatingsByRoomClass(roomClassId, rating, pageable);

        RatingSummaryResponse feedbackData = feedbackPage.getContent().isEmpty()
                ? new RatingSummaryResponse()
                : feedbackPage.getContent().get(0);

        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", feedbackData.getAverageRating() != null ? feedbackData.getAverageRating() : 0.0);
        response.put("totalReviews", feedbackData.getTotalReviews() != null ? feedbackData.getTotalReviews() : 0L);
        response.put("ratingDistribution", feedbackData.getRatingDistribution());
        response.put("content", feedbackData.getContent());
        response.put("currentPage", feedbackPage.getNumber());
        response.put("totalPages", feedbackPage.getTotalPages());
        response.put("totalItems", feedbackPage.getTotalElements());
        response.put("pageSize", feedbackPage.getSize());
        response.put("hasNext", feedbackPage.hasNext());
        response.put("hasPrevious", feedbackPage.hasPrevious());

        return ResponseEntity.ok(response);
    }
}
