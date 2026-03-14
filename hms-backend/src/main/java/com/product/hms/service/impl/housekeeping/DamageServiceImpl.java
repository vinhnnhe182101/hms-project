// service/impl/housekeeping/DamageServiceImpl.java
package com.product.hms.service.impl.housekeeping;

import com.product.hms.dto.request.DamageReportRequest;
import com.product.hms.dto.response.DamageReportResponse;
import com.product.hms.entity.*;
import com.product.hms.enums.FolioItemStatus;
import com.product.hms.enums.FolioItemType;
import com.product.hms.exception.BadRequest;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.ResourceNotFoundException;
import com.product.hms.repository.*;
import com.product.hms.service.housekeeping.DamageService;
import com.product.hms.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DamageServiceImpl implements DamageService {

    private final DamageReportRepository damageReportRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final FolioRepository folioRepository;
    private final FolioItemRepository folioItemRepository;
    private final SecurityUtil securityUtil;

    @Override
    public DamageReportResponse reportDamage(DamageReportRequest request) {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();
        log.info("Staff {} reporting damage for room: {}",
                currentStaff.getFullName(), request.getRoomId());

        // Validate room
        RoomEntity room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.ROOM_NOT_FOUND.code() + ": Room not found"));

        // Validate reservation
        ReservationEntity reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND.code() + ": Reservation not found"));

        // Create damage report
        DamageReportEntity report = new DamageReportEntity();
        report.setRoomEntity(room);
        report.setReportedByStaffEntity(currentStaff);
        report.setReservationEntity(reservation);
        report.setDescription(request.getDescription());
        report.setQuantity(request.getQuantity());
        report.setPenaltyAmount(BigDecimal.valueOf(request.getPenaltyAmount()));
        report.setStatus("OPEN");
        report.setIsActive(true);

        DamageReportEntity saved = damageReportRepository.save(report);
        log.info("Damage report {} created with penalty ${}",
                saved.getId(), saved.getPenaltyAmount());

        // Add penalty to folio
        if (saved.getPenaltyAmount().compareTo(BigDecimal.ZERO) > 0) {
            addPenaltyToFolio(saved);
        }

        return convertToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageReportResponse> getMyDamageReports() {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();

        return damageReportRepository
                .findByReportedByStaffEntityId(currentStaff.getId())
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DamageReportResponse resolveDamage(Long reportId) {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();
        log.info("Staff {} resolving damage report {}", currentStaff.getFullName(), reportId);

        DamageReportEntity report = damageReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.DAMAGE_REPORT_NOT_FOUND.code() + ": Damage report not found"));

        if ("RESOLVED".equals(report.getStatus())) {
            throw new BadRequest(
                    ErrorCode.DAMAGE_REPORT_ALREADY_RESOLVED.code() +
                            ": Damage report already resolved");
        }

        report.setStatus("RESOLVED");
        DamageReportEntity saved = damageReportRepository.save(report);

        return convertToResponse(saved);
    }

    private void addPenaltyToFolio(DamageReportEntity report) {
        // Find reservation room
        ReservationRoomEntity reservationRoom = reservationRoomRepository
                .findByReservationEntityId(report.getReservationEntity().getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESERVATION_ROOM_NOT_FOUND.code() + ": Reservation room not found"));

        // Get folio
        FolioEntity folio = folioRepository
                .findByReservationRoomEntityId(reservationRoom.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.FOLIO_NOT_FOUND.code() + ": Folio not found"));

        // Create folio item
        FolioItemEntity folioItem = new FolioItemEntity();
        folioItem.setFolioEntity(folio);
        folioItem.setType(FolioItemType.DAMAGE_PENALTY);
        folioItem.setDescription(report.getDescription());
        folioItem.setQuantity(report.getQuantity());
        folioItem.setTotalPrice(report.getPenaltyAmount());
        folioItem.setStatus(FolioItemStatus.UNPAID);
        folioItemRepository.save(folioItem);

        // Update folio
        folio.setTotalCharges(folio.getTotalCharges().add(report.getPenaltyAmount()));
        folio.setBalance(folio.getBalance().add(report.getPenaltyAmount()));
        folioRepository.save(folio);

        log.info("Added penalty ${} to folio {}", report.getPenaltyAmount(), folio.getId());
    }

    private DamageReportResponse convertToResponse(DamageReportEntity report) {
        return DamageReportResponse.builder()
                .id(report.getId())
                .roomNumber(report.getRoomEntity().getRoomNumber())
                .description(report.getDescription())
                .quantity(report.getQuantity())
                .penaltyAmount(report.getPenaltyAmount().doubleValue())
                .status(report.getStatus())
                .createdAt(Timestamp.from(Instant.now())) // Add createdAt to entity if needed
                .reportedBy(report.getReportedByStaffEntity().getFullName())
                .build();
    }
}