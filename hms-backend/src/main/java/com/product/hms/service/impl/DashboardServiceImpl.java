package com.product.hms.service.impl;

import com.product.hms.dto.response.dashboard.AdminDashboardResponse;
import com.product.hms.dto.response.dashboard.DashboardStatResponse;
import com.product.hms.dto.response.dashboard.RecentBookingDto;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.enums.RoomStatus;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.repository.RoomRepository;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.repository.ReservationRepository;
import com.product.hms.repository.PaymentTransactionRepository;
import com.product.hms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public AdminDashboardResponse getDashboardData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.with(LocalTime.MIN);
        LocalDateTime endOfDay = now.with(LocalTime.MAX);

        long totalRooms = roomRepository.countByIsActiveTrue();
        long occupiedRooms = roomRepository.countByStatusAndIsActiveTrue(RoomStatus.OCCUPIED);
        long dirtyRooms = roomRepository.countByStatusAndIsActiveTrue(RoomStatus.DIRTY);
        long checkInsToday = reservationRepository.countCheckInsToday(startOfDay, endOfDay);
        long checkOutsToday = reservationRepository.countCheckOutsToday(startOfDay, endOfDay);
        BigDecimal revenueToday = paymentTransactionRepository.sumRevenueToday(startOfDay, endOfDay);
        if (revenueToday == null) revenueToday = BigDecimal.ZERO;
        long totalGuests = customerRepository.countByIsActiveTrue();
        long pendingReservations = reservationRepository.countByStatusAndIsActiveTrue(ReservationStatus.PENDING_DEPOSIT);

        DashboardStatResponse stats = new DashboardStatResponse(
                totalRooms,
                occupiedRooms,
                dirtyRooms,
                checkInsToday,
                checkOutsToday,
                revenueToday,
                totalGuests,
                pendingReservations
        );

        List<ReservationEntity> recent = reservationRepository.findTop5ByIsActiveTrueOrderByCreatedAtDesc();
        List<RecentBookingDto> recentBookings = recent.stream().map(r -> new RecentBookingDto(
                r.getId(),
                r.getCode(),
                r.getCustomerEntity() != null ? r.getCustomerEntity().getFullName() : "",
                r.getExpectedCheckIn() != null ? r.getExpectedCheckIn().toLocalDateTime() : null,
                r.getExpectedCheckOut() != null ? r.getExpectedCheckOut().toLocalDateTime() : null,
                r.getStatus() != null ? r.getStatus().name() : null
        )).collect(Collectors.toList());

        return new AdminDashboardResponse(stats, recentBookings);
    }
}
