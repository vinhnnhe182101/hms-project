// service/impl/customer/CustomerBookingServiceImpl.java
package com.product.hms.service.impl;

import com.product.hms.dto.response.BookingDetailResponse;
import com.product.hms.dto.response.BookingHistoryResponse;
import com.product.hms.entity.*;
import com.product.hms.enums.FolioItemType;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.exception.BadRequest;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.ResourceNotFoundException;
import com.product.hms.repository.*;
import com.product.hms.service.CustomerBookingService;
import com.product.hms.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerBookingServiceImpl implements CustomerBookingService {

    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final FolioRepository folioRepository;
    private final FolioItemRepository folioItemRepository;
    private final RatingRepository ratingRepository;
    private final SecurityUtil securityUtil;

    @Override
    public List<BookingHistoryResponse> getBookingHistory() {
        CustomerEntity currentCustomer = securityUtil.getCurrentCustomer();
        log.info("Fetching booking history for customer: {}", currentCustomer.getEmail());

        List<ReservationEntity> reservations = reservationRepository
                .findByCustomerEntityIdOrderByCreatedAtDesc(currentCustomer.getId());

        return reservations.stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDetailResponse getBookingDetails(Long bookingId) {
        CustomerEntity currentCustomer = securityUtil.getCurrentCustomer();
        log.info("Fetching booking details for customer: {}, booking: {}",
                currentCustomer.getEmail(), bookingId);

        ReservationEntity reservation = reservationRepository
                .findByIdAndCustomerEntityId(bookingId, currentCustomer.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND.code() + ": Booking not found"));

        return convertToDetailResponse(reservation);
    }

    @Override
    public List<BookingHistoryResponse> getUpcomingBookings() {
        CustomerEntity currentCustomer = securityUtil.getCurrentCustomer();

        List<ReservationEntity> reservations = reservationRepository
                .findByCustomerEntityIdAndStatusIn(
                        currentCustomer.getId(),
                        List.of(ReservationStatus.PENDING_DEPOSIT, ReservationStatus.CONFIRMED, ReservationStatus.IN_HOUSE)
                );

        return reservations.stream()
                .map(this::convertToHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, String reason) {
        CustomerEntity currentCustomer = securityUtil.getCurrentCustomer();

        ReservationEntity reservation = reservationRepository
                .findByIdAndCustomerEntityId(bookingId, currentCustomer.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND.code() + ": Booking not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING_DEPOSIT &&
                reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BadRequest(
                    ErrorCode.RESERVATION_CANCEL_NOT_ALLOWED.code() +
                            ": Cannot cancel booking with status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setNote(reason);
        reservationRepository.save(reservation);

        log.info("Booking {} cancelled by customer {}", bookingId, currentCustomer.getEmail());
    }

    private BookingHistoryResponse convertToHistoryResponse(ReservationEntity reservation) {
        List<ReservationRoomEntity> rooms = reservationRoomRepository
                .findByReservationEntityId(reservation.getId());

        String roomType = rooms.stream()
                .map(r -> r.getRoomClassEntity().getName())
                .findFirst()
                .orElse("Unknown");

        String roomNumber = rooms.stream()
                .map(r -> r.getRoomEntity() != null ? r.getRoomEntity().getRoomNumber() : null)
                .filter(rn -> rn != null)
                .findFirst()
                .orElse(null);

        Integer nights = (int) ChronoUnit.DAYS.between(
                reservation.getExpectedCheckIn().toLocalDateTime(),
                reservation.getExpectedCheckOut().toLocalDateTime());

        BigDecimal totalPrice = rooms.stream()
                .map(ReservationRoomEntity::getPriceAtBooking)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<FolioEntity> folios = folioRepository.findByReservationEntityId(reservation.getId());
        BigDecimal paidAmount = folios.stream()
                .map(FolioEntity::getTotalPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalPrice.subtract(paidAmount);

        // KIỂM TRA XEM ĐÃ REVIEW CHƯA
        boolean hasReviewed = ratingRepository.existsByReservationEntityId(reservation.getId());

        return BookingHistoryResponse.builder()
                .id(reservation.getId())
                .code(reservation.getCode())
                .status(reservation.getStatus())
                .checkIn(reservation.getExpectedCheckIn())
                .checkOut(reservation.getExpectedCheckOut())
                .nights(nights)
                .roomType(roomType)
                .roomNumber(roomNumber)
                .adults(reservation.getNumberOfMembers())
                .children(0)
                .totalPrice(totalPrice)
                .paidAmount(paidAmount)
                .balance(balance)
                .createdAt(reservation.getCreatedAt())
                .hasReviewed(hasReviewed)  // THÊM DÒNG NÀY
                .build();
    }

    private BookingDetailResponse convertToDetailResponse(ReservationEntity reservation) {
        List<ReservationRoomEntity> rooms = reservationRoomRepository
                .findByReservationEntityId(reservation.getId());

        String roomType = rooms.stream()
                .map(r -> r.getRoomClassEntity().getName())
                .findFirst()
                .orElse("Unknown");

        String roomNumber = rooms.stream()
                .map(r -> r.getRoomEntity() != null ? r.getRoomEntity().getRoomNumber() : null)
                .filter(rn -> rn != null)
                .findFirst()
                .orElse(null);

        LocalDateTime checkIn = reservation.getExpectedCheckIn().toLocalDateTime();
        LocalDateTime checkOut = reservation.getExpectedCheckOut().toLocalDateTime();
        Integer nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);

        BigDecimal totalPrice = rooms.stream()
                .map(ReservationRoomEntity::getPriceAtBooking)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<FolioEntity> folios = folioRepository.findByReservationEntityId(reservation.getId());
        List<Long> folioIds = folios.stream().map(FolioEntity::getId).collect(Collectors.toList());

        List<FolioItemEntity> allItems = folioIds.isEmpty() ? new ArrayList<>() :
                folioItemRepository.findByFolioIds(folioIds);

        BigDecimal paidAmount = folios.stream()
                .map(FolioEntity::getTotalPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalPrice.subtract(paidAmount);

        // Lấy payments từ folio items (có thể cần join với payment_transaction)
        List<BookingDetailResponse.PaymentInfo> payments = new ArrayList<>();

        // Lấy services
        List<BookingDetailResponse.ServiceInfo> services = allItems.stream()
                .filter(item -> item.getType() == FolioItemType.SERVICE_CHARGE)
                .map(item -> BookingDetailResponse.ServiceInfo.builder()
                        .name(item.getDescription())
                        .quantity(item.getQuantity())
                        .price(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        // Lấy minibar
        List<BookingDetailResponse.MinibarInfo> minibar = allItems.stream()
                .filter(item -> item.getType() == FolioItemType.MINIBAR_CHARGE)
                .map(item -> BookingDetailResponse.MinibarInfo.builder()
                        .name(item.getDescription())
                        .quantity(item.getQuantity())
                        .price(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        // Lấy damages
        List<BookingDetailResponse.DamageInfo> damages = allItems.stream()
                .filter(item -> item.getType() == FolioItemType.DAMAGE_PENALTY)
                .map(item -> BookingDetailResponse.DamageInfo.builder()
                        .description(item.getDescription())
                        .quantity(item.getQuantity())
                        .penalty(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return BookingDetailResponse.builder()
                .id(reservation.getId())
                .code(reservation.getCode())
                .status(reservation.getStatus())
                .checkIn(reservation.getExpectedCheckIn())
                .checkOut(reservation.getExpectedCheckOut())
                .nights(nights)
                .roomType(roomType)
                .roomNumber(roomNumber)
                .adults(reservation.getNumberOfMembers())
                .children(0)
                .totalPrice(totalPrice)
                .paidAmount(paidAmount)
                .balance(balance)
                .payments(payments)
                .services(services)
                .minibar(minibar)
                .damages(damages)
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}