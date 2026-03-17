// service/impl/customer/CustomerReviewServiceImpl.java
package com.product.hms.service.impl;

import com.product.hms.dto.request.ReviewRequest;
import com.product.hms.dto.request.UpdateReviewRequest;
import com.product.hms.dto.response.ReviewResponse;
import com.product.hms.entity.*;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.exception.BadRequest;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.ResourceNotFoundException;
import com.product.hms.repository.*;
import com.product.hms.service.CustomerReviewService;
import com.product.hms.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerReviewServiceImpl implements CustomerReviewService {

    private final RatingRepository ratingRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final SecurityUtil securityUtil;

    @Override
    public ReviewResponse submitReview(ReviewRequest request) {
        CustomerEntity currentCustomer = securityUtil.getCurrentCustomer();
        log.info("Submitting review for customer: {}, booking: {}", 
                currentCustomer.getEmail(), request.getBookingId());

        // Kiểm tra booking tồn tại và thuộc về customer
        ReservationEntity reservation = reservationRepository
                .findByIdAndCustomerEntityId(request.getBookingId(), currentCustomer.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND.code() + ": Booking not found"));

        // Kiểm tra booking đã FINISHED chưa
        if (reservation.getStatus() != ReservationStatus.FINISHED) {
            throw new BadRequest(
                    ErrorCode.REVIEW_NOT_ALLOWED.code() + 
                    ": Only finished bookings can be reviewed");
        }

        // Kiểm tra đã review chưa
        if (ratingRepository.existsByReservationEntityId(reservation.getId())) {
            throw new BadRequest(
                    ErrorCode.REVIEW_ALREADY_EXISTS.code() + 
                    ": You have already reviewed this booking");
        }

        // Tạo review mới
        RatingEntity review = new RatingEntity();
        review.setReservationEntity(reservation);
        review.setCustomerEntity(currentCustomer);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setReviewDate(Timestamp.from(Instant.now()));
        review.setIsPublic(true);
        review.setIsActive(true);

        RatingEntity savedReview = ratingRepository.save(review);
        log.info("Review submitted successfully with id: {}", savedReview.getId());

        return convertToResponse(savedReview);
    }

    @Override
    public List<ReviewResponse> getMyReviews() {
        CustomerEntity currentCustomer = securityUtil.getCurrentCustomer();
        log.info("Fetching reviews for customer: {}", currentCustomer.getEmail());

        return ratingRepository.findByCustomerEntityIdOrderByReviewDateDesc(currentCustomer.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request) {
        CustomerEntity currentCustomer = securityUtil.getCurrentCustomer();
        log.info("Updating review {} for customer: {}", reviewId, currentCustomer.getEmail());

        RatingEntity review = ratingRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.REVIEW_NOT_FOUND.code() + ": Review not found"));

        if (!review.getCustomerEntity().getId().equals(currentCustomer.getId())) {
            throw new BadRequest(
                    ErrorCode.REVIEW_NOT_OWNER.code() + 
                    ": You can only update your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        RatingEntity updatedReview = ratingRepository.save(review);
        log.info("Review updated successfully");

        return convertToResponse(updatedReview);
    }

    @Override
    public void deleteReview(Long reviewId) {
        CustomerEntity currentCustomer = securityUtil.getCurrentCustomer();
        log.info("Deleting review {} for customer: {}", reviewId, currentCustomer.getEmail());

        RatingEntity review = ratingRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.REVIEW_NOT_FOUND.code() + ": Review not found"));

        if (!review.getCustomerEntity().getId().equals(currentCustomer.getId())) {
            throw new BadRequest(
                    ErrorCode.REVIEW_NOT_OWNER.code() + 
                    ": You can only delete your own reviews");
        }

        // Soft delete
        review.setIsActive(false);
        ratingRepository.save(review);
        log.info("Review deleted successfully");
    }

    @Override
    public boolean hasReviewed(Long bookingId) {
        return ratingRepository.existsByReservationEntityId(bookingId);
    }

    private ReviewResponse convertToResponse(RatingEntity review) {
        ReservationEntity reservation = review.getReservationEntity();
        
        // Lấy thông tin phòng từ reservation_room
        String roomNumber = null;
        String roomType = null;
        
        List<ReservationRoomEntity> rooms = reservationRoomRepository
                .findByReservationEntityId(reservation.getId());
        
        if (!rooms.isEmpty()) {
            ReservationRoomEntity room = rooms.get(0);
            if (room.getRoomEntity() != null) {
                roomNumber = room.getRoomEntity().getRoomNumber();
            }
            roomType = room.getRoomClassEntity().getName();
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .bookingCode(reservation.getCode())
                .roomNumber(roomNumber)
                .roomType(roomType)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getReviewDate())
                .isPublic(review.getIsPublic())
                .build();
    }
}