package com.product.hms.service.impl;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.response.*;
import com.product.hms.entity.*;
import com.product.hms.enums.*;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.*;
import com.product.hms.service.FolioService;
import com.product.hms.service.PaymentService;
import com.product.hms.service.ReservationRoomService;
import com.product.hms.service.impl.reservation.ReservationCheckOutSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationRoomServiceImpl implements ReservationRoomService {

    private final ReservationRoomRepository reservationRoomRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final FolioRepository folioRepository;
    private final FolioItemRepository folioItemRepository;
    private final RoomOccupantRepository roomOccupantRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final FolioService folioService;
    private final PaymentService paymentService;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public ReservationRoomFolioResponse getReservationRoomFolio(Long reservationRoomId) {
        ReservationRoomEntity reservationRoom = reservationRoomRepository.findById(reservationRoomId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_ROOM_NOT_FOUND,
                        "Reservation room not found with ID: " + reservationRoomId
                ));

        // Get folio
        FolioEntity folio = folioRepository.findByReservationRoom(reservationRoom)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservation room ID: " + reservationRoomId
                ));

        // Get folio items
        List<FolioItemEntity> folioItems = folioItemRepository.findByFolioEntityAndIsActiveTrue(folio);
        List<FolioItemResponse> folioItemResponses = folioItems.stream()
                .map(item -> new FolioItemResponse(
                        item.getId(),
                        item.getType().getDbValue(),
                        item.getDescription(),
                        item.getQuantity(),
                        item.getTotalPrice(),
                        item.getStatus().getDbValue()
                ))
                .toList();

        // Get room occupants
        List<RoomOccupantEntity> occupants = roomOccupantRepository
                .findByReservationRoomEntityAndIsActiveTrue(reservationRoom);
        List<RoomOccupantResponse> occupantResponses = occupants.stream()
                .map(occupant -> new RoomOccupantResponse(
                        customerMapper.toResponse(occupant.getCustomerEntity()),
                        occupant.getRole()
                ))
                .toList();

        // Get room info
        String roomNumber = reservationRoom.getRoomEntity() != null
                ? reservationRoom.getRoomEntity().getRoomNumber()
                : "Not assigned";
        String roomClassName = reservationRoom.getRoomClassEntity().getName();

        return new ReservationRoomFolioResponse(
                reservationRoom.getId(),
                roomNumber,
                roomClassName,
                occupantResponses,
                folioItemResponses,
                folio.getTotalCharges(),
                folio.getTotalPaid(),
                folio.getBalance()
        );
    }

    @Override
    @Transactional
    public ReservationRoomCheckOutResponse checkOutReservationRoom(Long reservationRoomId) {
        ReservationRoomEntity reservationRoom = reservationRoomRepository.findById(reservationRoomId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_ROOM_NOT_FOUND,
                        "Reservation room not found with ID: " + reservationRoomId
                ));

        ReservationEntity reservation = reservationRoom.getReservationEntity();

        // Validate reservation is IN_HOUSE
        if (reservation.getStatus() != ReservationStatus.IN_HOUSE) {
            throw new BusinessException(
                    ErrorCode.RESERVATION_CHECKOUT_NOT_ALLOWED,
                    "Check-out only allowed when reservation status is IN_HOUSE. Current: " + reservation.getStatus()
            );
        }

        // Validate room is CHECKED_IN
        if (reservationRoom.getStatus() != ReservationRoomStatus.CHECKED_IN) {
            throw new BusinessException(
                    ErrorCode.RESERVATION_ROOM_NOT_CHECKED_IN,
                    "Room must be in CHECKED_IN status to check out. Room ID: " + reservationRoomId
            );
        }

        // Check for pending services
        boolean hasPendingServices = serviceBookingRepository.existsByReservationRoomEntityAndStatus(
                reservationRoom,
                ServiceBookingStatus.PENDING
        );
        if (hasPendingServices) {
            throw new BusinessException(
                    ErrorCode.RESERVATION_ROOM_HAS_PENDING_SERVICES,
                    "Cannot check out room with pending services. Room ID: " + reservationRoomId
            );
        }

        // Set actual checkout time
        reservationRoom.setActualCheckOut(Instant.now());

        // Apply late check-out fee if applicable
        BigDecimal lateCheckOutFee = ReservationCheckOutSupport.calculateLateCheckOutFee(
                reservationRoom,
                reservation.getExpectedCheckOut()
        );
        if (lateCheckOutFee.signum() > 0) {
            folioService.applyLateCheckOutFee(reservationRoom, lateCheckOutFee);
        }

        // Update room status
        reservationRoom.setStatus(ReservationRoomStatus.CHECKED_OUT);
        reservationRoomRepository.save(reservationRoom);

        // Update physical room status to DIRTY
        if (reservationRoom.getRoomEntity() != null) {
            reservationRoom.getRoomEntity().setStatus(RoomStatus.DIRTY);
            roomRepository.save(reservationRoom.getRoomEntity());
        }

        // Check if all rooms are checked out, update reservation status
        updateReservationStatusIfAllCheckedOut(reservation);

        return new ReservationRoomCheckOutResponse(
                reservationRoomId,
                ReservationRoomStatus.CHECKED_OUT.getDbValue(),
                "Room checked out successfully"
        );
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(Long reservationRoomId, PaymentRequest request) {
        ReservationRoomEntity reservationRoom = reservationRoomRepository.findById(reservationRoomId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_ROOM_NOT_FOUND,
                        "Reservation room not found with ID: " + reservationRoomId
                ));

        ReservationEntity reservation = reservationRoom.getReservationEntity();

        FolioEntity folio = folioRepository.findByReservationRoom(reservationRoom)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservation room ID: " + reservationRoomId
                ));

        if (folio.getStatus() == FolioStatus.CLOSED) {
            throw new BusinessException(
                    ErrorCode.FOLIO_ALREADY_CLOSED,
                    "Cannot process payment for closed folio"
            );
        }

        BigDecimal depositRequested = request.depositAmount() != null ? request.depositAmount() : BigDecimal.ZERO;
        if (depositRequested.signum() < 0) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Deposit amount must be >= 0");
        }

        BigDecimal depositAvailable = reservation.getTotalDeposit() != null
                ? reservation.getTotalDeposit()
                : BigDecimal.ZERO;

        if (depositRequested.compareTo(depositAvailable) > 0) {
            throw new BusinessException(
                    ErrorCode.INSUFFICIENT_DEPOSIT,
                    String.format("Insufficient deposit. Available: %s, Requested: %s", depositAvailable, depositRequested)
            );
        }

        PaymentResponse response = paymentService.processPaymentForFolio(folio, request);

        if (depositRequested.signum() > 0) {
            reservation.setTotalDeposit(depositAvailable.subtract(depositRequested));
            reservationRepository.save(reservation);
        }

        if (folio.getBalance().signum() <= 0) {
            folio.setStatus(FolioStatus.CLOSED);
            folioRepository.save(folio);
        }

        updateReservationStatusIfAllPaid(reservation);
        return response;
    }

    private void updateReservationStatusIfAllPaid(ReservationEntity reservation) {
        // Only update if reservation is CHECKED_OUT
        if (reservation.getStatus() != ReservationStatus.CHECKED_OUT) {
            return;
        }

        List<ReservationRoomEntity> allRooms = reservationRoomRepository
                .findByReservationEntity_IdAndIsActiveTrue(reservation.getId());

        boolean allPaid = allRooms.stream().allMatch(room -> {
            FolioEntity folio = folioRepository.findByReservationRoom(room).orElse(null);
            return folio != null && folio.getBalance().signum() <= 0;
        });

        if (allPaid) {
            reservation.setStatus(ReservationStatus.FINISHED);
            reservationRepository.save(reservation);
        }
    }

    private void updateReservationStatusIfAllCheckedOut(ReservationEntity reservation) {
        List<ReservationRoomEntity> allRooms = reservationRoomRepository
                .findByReservationEntity_IdAndIsActiveTrue(reservation.getId());

        boolean allCheckedOut = allRooms.stream()
                .allMatch(r -> r.getStatus() == ReservationRoomStatus.CHECKED_OUT);

        if (allCheckedOut) {
            reservation.setStatus(ReservationStatus.CHECKED_OUT);
            reservationRepository.save(reservation);
        }
    }
}

