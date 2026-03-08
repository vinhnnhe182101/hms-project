package com.product.hms.service.impl;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.request.ReservationCheckInRequest;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.*;
import com.product.hms.service.FolioService;
import com.product.hms.service.ReservationService;
import com.product.hms.service.RoomAllocationService;
import com.product.hms.service.impl.reservation.ReservationCheckInSupport;
import com.product.hms.service.impl.reservation.ReservationPricingSupport;
import com.product.hms.service.impl.reservation.ReservationResponseSupport;
import com.product.hms.service.impl.reservation.ReservationValidationSupport;
import com.product.hms.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ReservationService
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final RoomClassRepository roomClassRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final RoomRepository roomRepository;
    private final RoomAllocationService roomAllocationService;
    private final FolioService folioService;
    private final CustomerMapper customerMapper;


    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        ReservationValidationSupport.validateCreateReservationRequest(request);

        CustomerEntity customer = ReservationValidationSupport.resolveCustomer(request, customerRepository, customerMapper);
        Map<Long, RoomClassEntity> roomClassById = ReservationValidationSupport.loadAndValidateRoomClasses(
                request,
                roomClassRepository
        );

        BigDecimal depositAmount = ReservationPricingSupport.calculateDepositForRequest(request, roomClassById);
        ReservationEntity reservation = saveReservation(request, customer, depositAmount);

        createAllocationsAndFolios(reservation, request, roomClassById, depositAmount);
        return ReservationResponseSupport.buildReservationResponse(reservation, customer, roomAllocationService, customerMapper);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(Long reservationId, ReservationRequest request) {
        ReservationValidationSupport.validateCreateReservationRequest(request);

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found with ID: " + reservationId
                ));

        ReservationValidationSupport.validateUpdateWindow(reservation);
        CustomerEntity customer = ReservationValidationSupport.resolveCustomer(request, customerRepository, customerMapper);
        Map<Long, RoomClassEntity> roomClassById = ReservationValidationSupport.loadAndValidateRoomClasses(
                request,
                roomClassRepository
        );

        BigDecimal depositAmount = ReservationPricingSupport.calculateDepositForRequest(request, roomClassById);
        updateReservationFields(reservation, request, customer, depositAmount);
        reservationRepository.save(reservation);

        roomAllocationService.deleteAllocationsByReservation(reservation);
        createAllocationsAndFolios(reservation, request, roomClassById, depositAmount);

        return ReservationResponseSupport.buildReservationResponse(reservation, customer, roomAllocationService, customerMapper);
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found with ID: " + reservationId
                ));

        ReservationValidationSupport.validateCancellationAllowed(reservation);
        boolean isEligibleForRefund = ReservationValidationSupport.isRefundEligible(reservation);

        List<ReservationRoomEntity> allocations = roomAllocationService.getAllocationsByReservation(reservation);
        for (ReservationRoomEntity allocation : allocations) {
            if (isEligibleForRefund) {
                folioService.createRefundItem(allocation, reservation.getTotalDeposit());
            } else {
                folioService.createCancellationFeeItem(allocation, reservation.getTotalDeposit());
            }
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        return ReservationResponseSupport.buildReservationResponse(
                reservation,
                reservation.getCustomerEntity(),
                roomAllocationService,
                customerMapper
        );
    }

    @Override
    @Transactional
    public ReservationResponse checkInReservation(Long reservationId, ReservationCheckInRequest request) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found with ID: " + reservationId
                ));

        ReservationValidationSupport.validateCheckInRequest(request);
        ReservationValidationSupport.validateCheckInAllowed(reservation);

        ReservationCheckInSupport.assignRoomsForCheckIn(
                reservationId,
                request,
                reservationRoomRepository,
                roomRepository
        );

        applyEarlyCheckInFees(reservation);

        reservation.setStatus(ReservationStatus.IN_HOUSE);
        reservationRepository.save(reservation);

        return ReservationResponseSupport.buildReservationResponse(
                reservation,
                reservation.getCustomerEntity(),
                roomAllocationService,
                customerMapper
        );
    }

    private void applyEarlyCheckInFees(ReservationEntity reservation) {
        List<ReservationRoomEntity> allocations = reservationRoomRepository
                .findByReservationEntity_IdAndIsActiveTrue(reservation.getId());

        for (ReservationRoomEntity allocation : allocations) {
            BigDecimal fee = calculateEarlyCheckInFee(allocation, reservation.getExpectedCheckIn());
            if (fee.signum() > 0) {
                folioService.applyEarlyCheckInFee(allocation, fee);
            }
        }
    }

    private BigDecimal calculateEarlyCheckInFee(ReservationRoomEntity allocation, Timestamp expectedCheckIn) {
        if (allocation.getActualCheckIn() == null || expectedCheckIn == null) {
            return BigDecimal.ZERO;
        }

        Instant actualCheckIn = allocation.getActualCheckIn();
        Instant expected = expectedCheckIn.toInstant();
        if (!actualCheckIn.isBefore(expected)) {
            return BigDecimal.ZERO;
        }

        BigDecimal rate = determineEarlyCheckInRate(actualCheckIn, expectedCheckIn);
        if (rate.signum() <= 0 || allocation.getPriceAtBooking() == null) {
            return BigDecimal.ZERO;
        }

        return allocation.getPriceAtBooking().multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal determineEarlyCheckInRate(Instant actualCheckIn, Timestamp expectedCheckIn) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate actualDate = actualCheckIn.atZone(zoneId).toLocalDate();
        LocalDate expectedDate = expectedCheckIn.toInstant().atZone(zoneId).toLocalDate();

        // Check-in before expected date is treated as one extra night.
        if (actualDate.isBefore(expectedDate)) {
            return BigDecimal.ONE;
        }

        LocalTime checkInTime = actualCheckIn.atZone(zoneId).toLocalTime();
        if (checkInTime.isBefore(LocalTime.of(5, 0))) {
            return BigDecimal.ONE;
        }
        if (checkInTime.isBefore(LocalTime.of(9, 0))) {
            return new BigDecimal("0.50");
        }
        return BigDecimal.ZERO;
    }

    private void createAllocationsAndFolios(
            ReservationEntity reservation,
            ReservationRequest request,
            Map<Long, RoomClassEntity> roomClassById,
            BigDecimal depositAmount
    ) {
        List<ReservationRoomEntity> allocations = roomAllocationService.createRoomAllocations(
                reservation,
                request,
                roomClassById
        );

        for (ReservationRoomEntity allocation : allocations) {
            folioService.createFolioWithDepositItem(allocation, depositAmount);
        }
    }

    private ReservationEntity saveReservation(ReservationRequest request, CustomerEntity customer, BigDecimal depositAmount) {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setCode(RandomUtils.generateReservationCode("RS"));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setCreatedAt(Timestamp.from(Instant.now()));
        reservation.setIsActive(true);
        updateReservationFields(reservation, request, customer, depositAmount);
        return reservationRepository.save(reservation);
    }

    private void updateReservationFields(
            ReservationEntity reservation,
            ReservationRequest request,
            CustomerEntity customer,
            BigDecimal depositAmount
    ) {
        reservation.setCustomerEntity(customer);
        reservation.setExpectedCheckIn(request.checkInDate());
        reservation.setExpectedCheckOut(request.checkOutDate());
        reservation.setTotalDeposit(depositAmount);
        reservation.setNumberOfMembers(request.numberOfMembers() != null ? request.numberOfMembers() : 1);
        reservation.setNote(request.note());
    }
}
