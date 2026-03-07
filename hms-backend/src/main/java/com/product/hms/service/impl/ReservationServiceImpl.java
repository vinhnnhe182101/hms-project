package com.product.hms.service.impl;

import com.product.hms.constants.Reservation;
import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.request.RoomClassQuantityRequest;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.dto.response.RoomClassQuantityResponse;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.repository.ReservationRepository;
import com.product.hms.repository.RoomClassRepository;
import com.product.hms.service.FolioService;
import com.product.hms.service.ReservationService;
import com.product.hms.service.RoomAllocationService;
import com.product.hms.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ReservationService
 */
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    private final RoomClassRepository roomClassRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final RoomAllocationService roomAllocationService;
    private final FolioService folioService;
    private final CustomerMapper customerMapper;

    /**
     * Generates a booking reference in the format: PREFIX + YYMMDD + 6 random characters
     *
     * @return A unique booking reference string
     */
    public static String generateReservationCode() {
        String prefix = "RS";
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        return prefix + datePart + RandomUtils.generateRandomString(6);
    }

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        validateCreateReservationRequest(request);

        CustomerEntity customer = resolveCustomer(request);
        Map<Long, RoomClassEntity> roomClassById = loadAndValidateRoomClasses(request);

        BigDecimal depositAmount = calculateDeposit(calculateTotalRoomCost(request, roomClassById));

        ReservationEntity reservation = saveReservation(request, customer, depositAmount);
        createAllocationsAndFolios(reservation, request, roomClassById, depositAmount);

        return buildReservationResponse(reservation, customer);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(Long reservationId, ReservationRequest request) {
        validateCreateReservationRequest(request);

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found with ID: " + reservationId
                ));
        validateUpdateWindow(reservation);

        CustomerEntity customer = resolveCustomer(request);
        Map<Long, RoomClassEntity> roomClassById = loadAndValidateRoomClasses(request);

        BigDecimal depositAmount = calculateDeposit(calculateTotalRoomCost(request, roomClassById));
        updateReservationFields(reservation, request, customer, depositAmount);
        reservationRepository.save(reservation);

        roomAllocationService.deleteAllocationsByReservation(reservation);
        createAllocationsAndFolios(reservation, request, roomClassById, depositAmount);

        return buildReservationResponse(reservation, customer);
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

    private void validateCreateReservationRequest(ReservationRequest request) {
        if (request == null) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "request must be provided");
        }
        if (request.customerRequest() == null) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "customerRequest must be provided");
        }
        if (request.roomClassQuantities() == null || request.roomClassQuantities().isEmpty()) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "roomClassQuantities must not be empty");
        }
        validateDateRange(request.checkInDate(), request.checkOutDate());
    }

    private void validateDateRange(Timestamp checkInDate, Timestamp checkOutDate) {
        if (checkInDate == null || checkOutDate == null || !checkOutDate.after(checkInDate)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_DATE_RANGE,
                    "checkOutDate must be after checkInDate"
            );
        }
    }

    private void validateUpdateWindow(ReservationEntity reservation) {
        Instant checkInTime = reservation.getExpectedCheckIn().toInstant();
        Instant updateDeadline = checkInTime.minus(24, ChronoUnit.HOURS);
        if (!Instant.now().isBefore(updateDeadline)) {
            throw new BusinessException(
                    ErrorCode.RESERVATION_UPDATE_LOCKED,
                    "Reservation cannot be updated within 24 hours before check-in"
            );
        }
    }

    private CustomerEntity resolveCustomer(ReservationRequest request) {
        if (request.customerRequest().customerId() != null) {
            return customerRepository.findById(request.customerRequest().customerId())
                    .orElseThrow(() -> new NotFoundException(
                            ErrorCode.CUSTOMER_NOT_FOUND,
                            "Customer not found with ID: " + request.customerRequest().customerId()
                    ));
        }

        CustomerEntity newCustomer = customerMapper.toEntity(request.customerRequest());
        newCustomer.setIsActive(true);
        return customerRepository.save(newCustomer);
    }

    private Map<Long, RoomClassEntity> loadAndValidateRoomClasses(ReservationRequest request) {
        Map<Long, RoomClassEntity> roomClassById = new HashMap<>();

        for (RoomClassQuantityRequest roomClassQuantity : request.roomClassQuantities()) {
            if (roomClassQuantity == null
                    || roomClassQuantity.roomClassId() == null
                    || roomClassQuantity.numberOfPeople() == null
                    || roomClassQuantity.numberOfPeople() < 1) {
                throw new BadRequestException(
                        ErrorCode.INVALID_REQUEST,
                        "Each roomClassQuantity must include roomClassId and numberOfPeople >= 1"
                );
            }

            Long roomClassId = roomClassQuantity.roomClassId();
            Integer numberOfPeople = roomClassQuantity.numberOfPeople();

            RoomClassEntity roomClass = roomClassById.computeIfAbsent(roomClassId, id -> roomClassRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(
                            ErrorCode.ROOM_CLASS_NOT_FOUND,
                            "Room class not found with ID: " + id
                    )));

            if (!roomClass.getIsActive()) {
                throw new BusinessException(
                        ErrorCode.ROOM_CLASS_INACTIVE,
                        "Room class is not active: " + roomClassId
                );
            }

            if (numberOfPeople > roomClass.getMaxCapacity()) {
                throw new BadRequestException(
                        ErrorCode.EXCEED_MAX_CAPACITY,
                        String.format(
                                "Number of people (%d) exceeds max capacity (%d) for room class: %s",
                                numberOfPeople, roomClass.getMaxCapacity(), roomClass.getName()
                        )
                );
            }
        }

        return roomClassById;
    }

    private ReservationEntity saveReservation(ReservationRequest request, CustomerEntity customer, BigDecimal depositAmount) {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setCode(generateReservationCode());
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

    private ReservationResponse buildReservationResponse(ReservationEntity reservation, CustomerEntity customer) {
        List<RoomClassQuantityResponse> allocationResponses = new ArrayList<>();
        List<ReservationRoomEntity> allocations = roomAllocationService.getAllocationsByReservation(reservation);
        for (ReservationRoomEntity allocation : allocations) {
            allocationResponses.add(new RoomClassQuantityResponse(
                    allocation.getId(),
                    allocation.getRoomClassEntity().getId(),
                    allocation.getNumberOfPeople()
            ));
        }

        return new ReservationResponse(
                reservation.getId(),
                reservation.getCode(),
                customerMapper.toResponse(customer),
                allocationResponses,
                reservation.getExpectedCheckIn(),
                reservation.getExpectedCheckOut(),
                reservation.getStatus().name(),
                reservation.getNumberOfMembers(),
                reservation.getNote(),
                reservation.getCreatedAt()
        );
    }

    private BigDecimal calculateRoomCostForAllocation(RoomClassEntity roomClass, Integer numberOfPeople) {
        BigDecimal baseCost = roomClass.getBasePrice();

        if (numberOfPeople > roomClass.getStandardCapacity()) {
            int extraPeople = numberOfPeople - roomClass.getStandardCapacity();
            BigDecimal extraFee = roomClass.getExtraPersonFee().multiply(BigDecimal.valueOf(extraPeople));
            baseCost = baseCost.add(extraFee);
        }
        return baseCost;
    }

    /**
     * Calculates total room cost including base price and extra person fees
     */
    private BigDecimal calculateTotalRoomCost(ReservationRequest request, Map<Long, RoomClassEntity> roomClassById) {
        return request.roomClassQuantities().stream()
                .map(roomClassQuantityRequest -> {
                    RoomClassEntity roomClass = roomClassById.get(roomClassQuantityRequest.roomClassId());
                    Integer numberOfPeople = roomClassQuantityRequest.numberOfPeople();
                    return calculateRoomCostForAllocation(roomClass, numberOfPeople);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates deposit amount (20% of total room cost)
     */
    private BigDecimal calculateDeposit(BigDecimal totalRoomCost) {
        return totalRoomCost.multiply(Reservation.DEPOSIT_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found with ID: " + reservationId
                ));

        validateCancellationAllowed(reservation);

        boolean isEligibleForRefund = isRefundEligible(reservation);
        List<ReservationRoomEntity> allocations = roomAllocationService.getAllocationsByReservation(reservation);

        // Process refund or cancellation fee for each allocation
        for (ReservationRoomEntity allocation : allocations) {
            if (isEligibleForRefund) {
                folioService.createRefundItem(allocation, reservation.getTotalDeposit());
            } else {
                folioService.createCancellationFeeItem(allocation, reservation.getTotalDeposit());
            }
        }

        // Update reservation status to CANCELLED
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        return buildReservationResponse(reservation, reservation.getCustomerEntity());
    }

    private void validateCancellationAllowed(ReservationEntity reservation) {
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException(
                    ErrorCode.RESERVATION_ALREADY_CANCELED,
                    "Reservation is already canceled: " + reservation.getCode()
            );
        }

        // NOTE: Chỉ cho phép hủy khi status là CONFIRMED, PENDING_DEPOSIT
        if (reservation.getStatus() != ReservationStatus.CONFIRMED &&
                reservation.getStatus() != ReservationStatus.PENDING_DEPOSIT) {
            throw new BusinessException(
                    ErrorCode.RESERVATION_CANCEL_NOT_ALLOWED,
                    "Cannot cancel reservation with status: " + reservation.getStatus()
            );
        }
    }

    private boolean isRefundEligible(ReservationEntity reservation) {
        Instant checkInTime = reservation.getExpectedCheckIn().toInstant();
        Instant cancelDeadline = checkInTime.minus(24, ChronoUnit.HOURS);
        return Instant.now().isBefore(cancelDeadline);
    }
}
