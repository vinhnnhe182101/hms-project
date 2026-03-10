package com.product.hms.service.impl;

import com.product.hms.dto.request.ServiceBookingRequest;
import com.product.hms.dto.request.UpdateServiceBookingRequest;
import com.product.hms.dto.response.ServiceBookingResponse;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.ServiceBookingEntity;
import com.product.hms.entity.ServiceEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.enums.ServiceBookingStatus;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.ReservationRoomRepository;
import com.product.hms.repository.ServiceBookingRepository;
import com.product.hms.repository.ServiceRepository;
import com.product.hms.service.FolioService;
import com.product.hms.service.ServiceBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import com.product.hms.dto.request.ServiceBookingRequestDTO;
import com.product.hms.dto.response.ActiveAllocationResponseDTO;

/**
 * Implementation of ServiceBookingService
 */
@Service
@RequiredArgsConstructor
public class ServiceBookingServiceImpl implements ServiceBookingService {

    private final ReservationRoomRepository reservationRoomRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final FolioService folioService;

    @Override
    public List<ActiveAllocationResponseDTO> getActiveAllocationsByCustomer(Long customerId) {
        List<ReservationStatus> statuses = List.of(ReservationStatus.CONFIRMED, ReservationStatus.IN_HOUSE);
        List<ReservationRoomEntity> allocations = reservationRoomRepository.findActiveAllocationsByCustomer(customerId, statuses);

        return allocations.stream().map(a -> {
            ActiveAllocationResponseDTO dto = new ActiveAllocationResponseDTO();
            dto.setAllocationId(a.getId());
            dto.setReservationId(a.getReservationEntity().getId());

            String roomName = (a.getRoomEntity() != null) ? "Phòng " + a.getRoomEntity().getRoomNumber() : "Phòng đang chờ xếp";
            String className = (a.getRoomClassEntity() != null) ? a.getRoomClassEntity().getName() : "";

            dto.setRoomNumber(roomName);
            dto.setRoomClassName(className);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createServiceBookings(ServiceBookingRequestDTO request) {
        if (request.getCustomerId() == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Dữ liệu đặt dịch vụ không hợp lệ");
        }

        for (ServiceBookingRequestDTO.ServiceItem item : request.getItems()) {
            ReservationRoomEntity reservationRoom = reservationRoomRepository.findById(item.getAllocationId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin phòng sử dụng"));

            ServiceEntity service = serviceRepository.findById(item.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ: " + item.getServiceId()));

            ServiceBookingEntity serviceBookingEntity = new ServiceBookingEntity();
            serviceBookingEntity.setReservationRoomEntity(reservationRoom);
            serviceBookingEntity.setServiceEntity(service);
            serviceBookingEntity.setQuantity(item.getQuantity());
            serviceBookingEntity.setPriceAtBooking(service.getPrice());
            serviceBookingEntity.setStatus(ServiceBookingStatus.PENDING);
            serviceBookingEntity.setIsActive(true);

            serviceBookingEntity = serviceBookingRepository.save(serviceBookingEntity);

            BigDecimal chargeAmount = service.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            folioService.updateServiceCharge(serviceBookingEntity, chargeAmount);
        }
    }

    @Override
    @Transactional
    public ServiceBookingResponse createServiceBooking(Long reservationRoomId, ServiceBookingRequest serviceBookingRequest) {
        validateRequest(serviceBookingRequest);

        ReservationRoomEntity reservationRoom = getReservationRoom(reservationRoomId);
        validateReservationStatus(reservationRoom);

        ServiceEntity service = getService(serviceBookingRequest.serviceId());
        validateServiceActive(service);

        ServiceBookingEntity serviceBooking = buildServiceBooking(reservationRoom, service, serviceBookingRequest);
        ServiceBookingEntity saved = serviceBookingRepository.save(serviceBooking);

        return buildResponse(saved);
    }

    private void validateRequest(ServiceBookingRequest request) {
        if (request == null) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "request must be provided");
        }
        if (request.serviceId() == null) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "serviceId must be provided");
        }
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "quantity must be greater than 0");
        }
    }

    private void validateUpdateRequest(UpdateServiceBookingRequest request) {
        if (request == null) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "request must be provided");
        }
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "quantity must be greater than 0");
        }
    }

    private ReservationRoomEntity getReservationRoom(Long reservationRoomId) {
        return reservationRoomRepository.findById(reservationRoomId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_ROOM_NOT_FOUND,
                        "Reservation room not found with ID: " + reservationRoomId
                ));
    }

    private void validateReservationStatus(ReservationRoomEntity reservationRoom) {
        ReservationStatus status = reservationRoom.getReservationEntity().getStatus();
        if (status != ReservationStatus.IN_HOUSE) {
            throw new BusinessException(
                    ErrorCode.SERVICE_BOOKING_NOT_ALLOWED,
                    "Service booking only allowed for checked-in reservations. Current status: " + status
            );
        }
    }

    private ServiceEntity getService(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.SERVICE_NOT_FOUND,
                        "Service not found with ID: " + serviceId
                ));
    }

    private void validateServiceActive(ServiceEntity service) {
        if (!service.getIsActive()) {
            throw new BusinessException(
                    ErrorCode.SERVICE_INACTIVE,
                    "Service is not active: " + service.getName()
            );
        }
    }

    private ServiceBookingEntity buildServiceBooking(
            ReservationRoomEntity reservationRoom,
            ServiceEntity service,
            ServiceBookingRequest request
    ) {
        ServiceBookingEntity serviceBooking = new ServiceBookingEntity();
        serviceBooking.setReservationRoomEntity(reservationRoom);
        serviceBooking.setServiceEntity(service);
        serviceBooking.setQuantity(request.quantity());
        serviceBooking.setPriceAtBooking(service.getPrice());
        serviceBooking.setStatus(ServiceBookingStatus.PENDING);
        serviceBooking.setIsActive(true);
        return serviceBooking;
    }

    @Override
    @Transactional
    public ServiceBookingResponse updateServiceBooking(Long reservationRoomId, Long serviceBookingId, UpdateServiceBookingRequest request) {
        validateUpdateRequest(request);

        ServiceBookingEntity serviceBooking = getServiceBookingScoped(reservationRoomId, serviceBookingId);
        validateServiceBookingPending(serviceBooking);

        // Only update quantity, not serviceId
        serviceBooking.setQuantity(request.quantity());

        BigDecimal newAmount = serviceBooking.getPriceAtBooking()
                .multiply(BigDecimal.valueOf(serviceBooking.getQuantity()));

        ServiceBookingEntity updated = serviceBookingRepository.save(serviceBooking);
        folioService.updateServiceCharge(updated, newAmount);

        return buildResponse(updated);
    }

    @Override
    @Transactional
    public ServiceBookingResponse cancelServiceBooking(Long reservationRoomId, Long serviceBookingId) {
        ServiceBookingEntity serviceBooking = getServiceBookingScoped(reservationRoomId, serviceBookingId);
        validateServiceBookingPending(serviceBooking);

        serviceBooking.setStatus(ServiceBookingStatus.CANCELLED);
        serviceBooking.setIsActive(false);
        ServiceBookingEntity canceled = serviceBookingRepository.save(serviceBooking);

        folioService.cancelServiceCharge(serviceBooking);

        return buildResponse(canceled);
    }

    private ServiceBookingResponse buildResponse(ServiceBookingEntity serviceBooking) {
        BigDecimal totalAmount = serviceBooking.getPriceAtBooking()
                .multiply(BigDecimal.valueOf(serviceBooking.getQuantity()));

        return new ServiceBookingResponse(
                serviceBooking.getId(),
                serviceBooking.getReservationRoomEntity().getId(),
                serviceBooking.getServiceEntity().getId(),
                serviceBooking.getServiceEntity().getName(),
                serviceBooking.getQuantity(),
                serviceBooking.getPriceAtBooking(),
                totalAmount,
                serviceBooking.getStatus().name(),
                null, // notes not stored in current schema
                Timestamp.from(Instant.now())
        );
    }

    private ServiceBookingEntity getServiceBookingScoped(Long reservationRoomId, Long serviceBookingId) {
        return serviceBookingRepository.findByIdAndReservationRoomEntity_Id(serviceBookingId, reservationRoomId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.SERVICE_BOOKING_NOT_FOUND,
                        "Service booking not found with ID: " + serviceBookingId
                ));
    }

    private void validateServiceBookingPending(ServiceBookingEntity serviceBooking) {
        if (serviceBooking.getStatus() != ServiceBookingStatus.PENDING) {
            throw new BusinessException(
                    ErrorCode.SERVICE_BOOKING_NOT_ALLOWED,
                    "Service booking update/cancel only allowed when status is PENDING. Current: " + serviceBooking.getStatus()
            );
        }
    }
}
