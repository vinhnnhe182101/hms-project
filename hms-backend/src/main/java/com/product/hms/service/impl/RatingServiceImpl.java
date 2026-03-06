package com.product.hms.service.impl;

import com.product.hms.dto.response.RatingSummaryResponse;
import com.product.hms.dto.response.RatingResponse;
import com.product.hms.entity.RatingEntity;
import com.product.hms.repository.RatingRepository;
import com.product.hms.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    @Override
    public Page<RatingSummaryResponse> getRatingsByRoomClass(Long roomClassId, Integer ratingFilter, Pageable pageable) {
        Page<RatingEntity> pageData;
        
        if (ratingFilter != null) {
            pageData = ratingRepository.findPublicRatingsByRoomClassIdAndRating(roomClassId, ratingFilter, pageable);
        } else {
            pageData = ratingRepository.findPublicRatingsByRoomClassId(roomClassId, pageable);
        }
        
        Double averageRating = ratingRepository.getAverageRatingByRoomClassId(roomClassId);
        Long totalReviews = ratingRepository.countPublicRatingsByRoomClassId(roomClassId);
        Map<Integer, Long> distribution = getRatingDistribution(roomClassId);

        SimpleDateFormat sdf = new SimpleDateFormat("dd 'Thg' MM, yyyy");

        return pageData.map(entity -> {
            List<RatingResponse> content = pageData.getContent().stream().map(e -> {
                RatingResponse res = new RatingResponse();
                res.setId(e.getId());
                res.setName(e.getCustomerEntity() != null ? e.getCustomerEntity().getFullName() : "Khách ẩn danh");
                if (res.getName() != null && !res.getName().isEmpty()) {
                    res.setAvatar(res.getName().substring(0, 1).toUpperCase());
                } else {
                    res.setAvatar("A");
                }
                res.setRating(e.getRating());
                res.setComment(e.getComment());
                if (e.getReviewDate() != null) {
                    res.setDate(sdf.format(e.getReviewDate()));
                } else {
                    res.setDate("Gần đây");
                }
                return res;
            }).collect(Collectors.toList());

            RatingSummaryResponse response = new RatingSummaryResponse();
            response.setAverageRating(averageRating != null ? averageRating : 0.0);
            response.setTotalReviews(totalReviews != null ? totalReviews : 0L);
            response.setRatingDistribution(distribution);
            response.setContent(content);

            return response;
        });
    }

    private Map<Integer, Long> getRatingDistribution(Long roomClassId) {
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }

        List<Object[]> results = ratingRepository.countRatingsByRatingForRoomClass(roomClassId);
        for (Object[] result : results) {
            Integer ratingValue = (Integer) result[0];
            Long count = (Long) result[1];
            distribution.put(ratingValue, count);
        }

        return distribution;
    }
}
