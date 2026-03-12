package com.product.hms.service.impl;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.request.RoomChangeRequest;
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
import com.product.hms.service.impl.reservation.ReservationRoomSupport;
import com.product.hms.service.impl.reservation.ReservationRoomValidationSupport;
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
        ReservationRoomEntity reservationRoomEntity = reservationRoomRepository.findById(reservationRoomId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_ROOM_NOT_FOUND,
                        "Reservation room not found with ID: " + reservationRoomId
                ));

        // Lấy folioEntity dùng hàm support
        FolioEntity folioEntity = ReservationRoomSupport.getFolioByReservationRoom(folioRepository, reservationRoomEntity);

        // Lấy folioEntity items dùng hàm support
        List<FolioItemEntity> folioItems = ReservationRoomSupport.getActiveFolioItemsByFolio(folioItemRepository, folioEntity);
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

        // Lấy room occupants dùng hàm support
        List<RoomOccupantEntity> occupants = ReservationRoomSupport.getActiveRoomOccupantsByReservationRoom(roomOccupantRepository, reservationRoomEntity);
        List<RoomOccupantResponse> occupantResponses = occupants.stream()
                .map(occupant -> new RoomOccupantResponse(
                        customerMapper.toResponse(occupant.getCustomerEntity()),
                        occupant.getRole()
                ))
                .toList();

        // Lấy thông tin phòng
        String roomNumber = reservationRoomEntity.getRoomEntity() != null
                ? reservationRoomEntity.getRoomEntity().getRoomNumber()
                : "Not assigned";
        String roomClassName = reservationRoomEntity.getRoomClassEntity().getName();

        return new ReservationRoomFolioResponse(
                reservationRoomEntity.getId(),
                roomNumber,
                roomClassName,
                occupantResponses,
                folioItemResponses,
                folioEntity.getTotalCharges(),
                folioEntity.getTotalPaid(),
                folioEntity.getBalance()
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
        ReservationRoomValidationSupport.validateReservationInHouseForCheckOut(reservation);
        ReservationRoomValidationSupport.validateReservationRoomCheckedInForCheckOut(reservationRoom);
        ReservationRoomValidationSupport.validateNoPendingServicesForCheckOut(reservationRoom, serviceBookingRepository);
        reservationRoom.setActualCheckOut(Instant.now());
        BigDecimal lateCheckOutFee = ReservationCheckOutSupport.calculateLateCheckOutFee(
                reservationRoom,
                reservation.getExpectedCheckOut()
        );
        if (lateCheckOutFee.signum() > 0) {
            folioService.applyLateCheckOutFee(reservationRoom, lateCheckOutFee);
        }
        reservationRoom.setStatus(ReservationRoomStatus.CHECKED_OUT);
        reservationRoomRepository.save(reservationRoom);
        if (reservationRoom.getRoomEntity() != null) {
            reservationRoom.getRoomEntity().setStatus(RoomStatus.DIRTY);
            roomRepository.save(reservationRoom.getRoomEntity());
        }
        ReservationRoomSupport.updateReservationStatusIfAllCheckedOut(reservation, reservationRoomRepository, reservationRepository);
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
        FolioEntity folio = folioRepository.findByReservationRoomEntity(reservationRoom)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservation room ID: " + reservationRoomId
                ));
        ReservationRoomValidationSupport.validateFolioNotClosed(folio);
        BigDecimal depositRequested = request.depositAmount() != null ? request.depositAmount() : BigDecimal.ZERO;
        BigDecimal depositAvailable = reservation.getTotalDeposit() != null
                ? reservation.getTotalDeposit()
                : BigDecimal.ZERO;
        ReservationRoomValidationSupport.validateDepositAmount(depositRequested, depositAvailable);
        PaymentResponse response = paymentService.processPaymentForFolio(folio, request);
        if (depositRequested.signum() > 0) {
            reservation.setTotalDeposit(depositAvailable.subtract(depositRequested));
            reservationRepository.save(reservation);
        }
        if (folio.getBalance().signum() <= 0) {
            folio.setStatus(FolioStatus.CLOSED);
            folioRepository.save(folio);
        }
        ReservationRoomSupport.updateReservationStatusIfAllPaid(reservation, reservationRoomRepository, folioRepository, reservationRepository);
        return response;
    }

    @Override
    @Transactional
    public void changeRoom(Long reservationRoomId, RoomChangeRequest request) {
        ReservationRoomEntity reservationRoom = reservationRoomRepository.findById(reservationRoomId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_ROOM_NOT_FOUND,
                        "Reservation room not found with ID: " + reservationRoomId
                ));
        ReservationEntity reservation = reservationRoom.getReservationEntity();
        ReservationRoomValidationSupport.validateReservationRoomForChange(reservation, reservationRoom);
        RoomEntity newRoom = roomRepository.findById(request.newRoomId())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.ROOM_NOT_FOUND,
                        "Room not found with ID: " + request.newRoomId()
                ));
        ReservationRoomValidationSupport.validateNewRoomForChange(newRoom, reservation, reservationRoomRepository);
        BigDecimal changeFee = ReservationRoomSupport.calculateRoomChangeFeeWithBookingPrice(newRoom, reservationRoom, reservation);
        ReservationRoomSupport.updateRoomStatusAndBookingPrice(reservationRoom, newRoom, roomRepository, reservationRoomRepository);
        folioService.handleRoomChangeAdjustment(reservationRoom, newRoom, changeFee);
        ReservationRoomSupport.updateNoteIfNeeded(request.note(), reservationRoom, reservationRoomRepository);
    }
}
