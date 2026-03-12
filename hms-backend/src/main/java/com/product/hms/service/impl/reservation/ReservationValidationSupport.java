package com.product.hms.service.impl.reservation;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.request.ReservationCheckInRequest;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.request.RoomClassQuantityRequest;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.repository.RoomClassRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public final class ReservationValidationSupport {

    private ReservationValidationSupport() {
    }

    public static void validateCreateReservationRequest(ReservationRequest request) {
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

    public static void validateDateRange(Timestamp checkInDate, Timestamp checkOutDate) {
        if (checkInDate == null || checkOutDate == null || !checkOutDate.after(checkInDate)) {
            throw new BadRequestException(
                    ErrorCode.INVALID_DATE_RANGE,
                    "checkOutDate must be after checkInDate"
            );
        }
    }

    public static void validateUpdateWindow(ReservationEntity reservation) {
        Instant checkInTime = reservation.getExpectedCheckIn().toInstant();
        Instant updateDeadline = checkInTime.minus(24, ChronoUnit.HOURS);
        if (!Instant.now().isBefore(updateDeadline)) {
            throw new BusinessException(
                    "Reservation cannot be updated within 24 hours before check-in"
            );
        }
    }

    public static CustomerEntity resolveCustomer(
            ReservationRequest request,
            CustomerRepository customerRepository,
            CustomerMapper customerMapper
    ) {
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

    public static Map<Long, RoomClassEntity> loadAndValidateRoomClasses(
            ReservationRequest request,
            RoomClassRepository roomClassRepository
    ) {
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

            if (!Boolean.TRUE.equals(roomClass.getIsActive())) {
                throw new BusinessException(
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

    public static void validateCancellationAllowed(ReservationEntity reservation) {
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException(
                    "Reservation is already canceled: " + reservation.getCode()
            );
        }

        if (reservation.getStatus() != ReservationStatus.CONFIRMED
                && reservation.getStatus() != ReservationStatus.PENDING_DEPOSIT) {
            throw new BusinessException(
                    "Cannot cancel reservation with status: " + reservation.getStatus()
            );
        }
    }

    public static boolean isRefundEligible(ReservationEntity reservation) {
        Instant checkInTime = reservation.getExpectedCheckIn().toInstant();
        Instant cancelDeadline = checkInTime.minus(24, ChronoUnit.HOURS);
        return Instant.now().isBefore(cancelDeadline);
    }

    public static void validateCheckInRequest(ReservationCheckInRequest request) {
        if (request == null) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "request must be provided");
        }
    }

    public static void validateCheckInAllowed(ReservationEntity reservation) {
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BusinessException(
                    "Check-in only allowed when reservation status is CONFIRMED. Current: " + reservation.getStatus()
            );
        }
    }
}

