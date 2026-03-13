package com.product.hms.api;

import com.product.hms.dto.response.RatingSummaryResponse;
import com.product.hms.dto.response.RoomClassResponse;
import com.product.hms.dto.response.ServiceResponse;
import com.product.hms.service.RatingService;
import com.product.hms.service.RoomClassService;
import com.product.hms.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeApi {

    private final RoomClassService roomClassService;
    private final ServiceService serviceService;
    private final RatingService ratingService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getHomeData() {
        Map<String, Object> response = new HashMap<>();

        // 1. Featured Rooms
        Pageable roomPageable = PageRequest.of(0, 5, Sort.by("id").ascending());
        response.put("featuredRooms", roomClassService.getAllRoomClasses(roomPageable).getContent());

        // 2. Services
        Pageable servicePageable = PageRequest.of(0, 5, Sort.by("id").ascending());
        response.put("services", serviceService.getAllServices(servicePageable).getContent());

        // 3. Latest Ratings
        Pageable ratingPageable = PageRequest.of(0, 3);
        Page<RatingSummaryResponse> feedbackPage = ratingService.getLatestRatings(ratingPageable);
        List<?> feedbackData = feedbackPage.getContent().isEmpty()
                ? List.of()
                : feedbackPage.getContent().get(0).getContent();
        response.put("testimonials", feedbackData);

        return ResponseEntity.ok(response);
    }
}
