package com.product.hms.service;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.response.CustomerResponse;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.repository.ReservationRepository;
import com.product.hms.repository.RoomClassRepository;
import com.product.hms.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationService cancellation feature
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService Cancel Tests")
class ReservationServiceCancelTest {

    private static final Long RESERVATION_ID = 10L;
    private static final Long CUSTOMER_ID = 2L;
    private static final String RESERVATION_CODE = "RS260307ABC123";
    private static final BigDecimal DEPOSIT_AMOUNT = BigDecimal.valueOf(200.00);

    @Mock
    private RoomClassRepository roomClassRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RoomAllocationService roomAllocationService;
    @Mock
    private FolioService folioService;
    @Mock
    private CustomerMapper customerMapper;

    private ReservationServiceImpl reservationService;

    private CustomerEntity customer;
    private CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationServiceImpl(
                roomClassRepository,
                customerRepository,
                reservationRepository,
                roomAllocationService,
                folioService,
                customerMapper
        );

        customer = new CustomerEntity();
        customer.setId(CUSTOMER_ID);
        customer.setFullName("Nguyen Van A");
        customer.setIsActive(true);

        customerResponse = new CustomerResponse(
                CUSTOMER_ID,
                "Nguyen Van A",
                "0900000000",
                "012345678901",
                "a@example.com",
                "ADULT"
        );
    }

    /**
     * Creates a mock room class for allocation
     */
    private RoomClassEntity createMockRoomClass() {
        RoomClassEntity roomClass = new RoomClassEntity();
        roomClass.setId(1L);
        roomClass.setName("Standard");
        roomClass.setBasePrice(BigDecimal.valueOf(100));
        roomClass.setIsActive(true);
        return roomClass;
    }

    /**
     * Creates a complete allocation with room class
     */
    private ReservationRoomEntity createAllocation(Long id) {
        ReservationRoomEntity allocation = new ReservationRoomEntity();
        allocation.setId(id);
        allocation.setRoomClassEntity(createMockRoomClass());
        allocation.setNumberOfPeople(2);
        return allocation;
    }

    @Test
    @DisplayName("Should cancel reservation successfully when >24h before check-in (full refund)")
    void cancelReservation_MoreThan24Hours_ShouldRefundDeposit() {
        // Given
        ReservationEntity reservation = createReservation(
                ReservationStatus.CONFIRMED,
                Instant.now().plus(3, ChronoUnit.DAYS)
        );

        ReservationRoomEntity allocation = createAllocation(100L);

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));
        when(roomAllocationService.getAllocationsByReservation(reservation)).thenReturn(List.of(allocation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);

        // When
        ReservationResponse response = reservationService.cancelReservation(RESERVATION_ID);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("CANCELLED");

        verify(reservationRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ReservationStatus.CANCELLED);

        // Verify refund folio item was created (full deposit refund)
        verify(folioService).createRefundItem(eq(allocation), eq(DEPOSIT_AMOUNT));
    }

    @Test
    @DisplayName("Should cancel reservation with no refund when <24h before check-in (cancellation fee)")
    void cancelReservation_LessThan24Hours_ShouldApplyCancellationFee() {
        // Given
        ReservationEntity reservation = createReservation(
                ReservationStatus.CONFIRMED,
                Instant.now().plus(10, ChronoUnit.HOURS)
        );

        ReservationRoomEntity allocation = createAllocation(100L);

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));
        when(roomAllocationService.getAllocationsByReservation(reservation)).thenReturn(List.of(allocation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);

        // When
        ReservationResponse response = reservationService.cancelReservation(RESERVATION_ID);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("CANCELLED");

        verify(reservationRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ReservationStatus.CANCELLED);

        // Verify cancellation fee was applied (no refund)
        verify(folioService).createCancellationFeeItem(eq(allocation), eq(DEPOSIT_AMOUNT));
    }

    @Test
    @DisplayName("Should throw NotFoundException when reservation does not exist")
    void cancelReservation_NotFound_ShouldThrowException() {
        // Given
        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reservationService.cancelReservation(RESERVATION_ID))
                .isInstanceOf(NotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when reservation is already canceled")
    void cancelReservation_AlreadyCanceled_ShouldThrowException() {
        // Given
        ReservationEntity reservation = createReservation(
                ReservationStatus.CANCELLED,
                Instant.now().plus(3, ChronoUnit.DAYS)
        );

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        // When & Then
        assertThatThrownBy(() -> reservationService.cancelReservation(RESERVATION_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already canceled")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_ALREADY_CANCELED);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when reservation status is CHECKED_IN")
    void cancelReservation_CheckedIn_ShouldThrowException() {
        // Given
        ReservationEntity reservation = createReservation(
                ReservationStatus.IN_HOUSE,
                Instant.now().minus(1, ChronoUnit.DAYS)
        );

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        // When & Then
        assertThatThrownBy(() -> reservationService.cancelReservation(RESERVATION_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot cancel reservation with status")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_CANCEL_NOT_ALLOWED);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when reservation status is CHECKED_OUT")
    void cancelReservation_CheckedOut_ShouldThrowException() {
        // Given
        ReservationEntity reservation = createReservation(
                ReservationStatus.CHECKED_OUT,
                Instant.now().minus(5, ChronoUnit.DAYS)
        );

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        // When & Then
        assertThatThrownBy(() -> reservationService.cancelReservation(RESERVATION_ID))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_CANCEL_NOT_ALLOWED);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle multiple allocations when canceling")
    void cancelReservation_MultipleAllocations_ShouldProcessAll() {
        // Given
        ReservationEntity reservation = createReservation(
                ReservationStatus.CONFIRMED,
                Instant.now().plus(5, ChronoUnit.DAYS)
        );

        ReservationRoomEntity allocation1 = createAllocation(100L);
        ReservationRoomEntity allocation2 = createAllocation(101L);

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));
        when(roomAllocationService.getAllocationsByReservation(reservation)).thenReturn(List.of(allocation1, allocation2));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        // When
        reservationService.cancelReservation(RESERVATION_ID);

        // Then
        verify(folioService, times(2)).createRefundItem(any(), eq(DEPOSIT_AMOUNT));
    }

    /**
     * Helper to create a reservation entity with given status and check-in time
     */
    private ReservationEntity createReservation(ReservationStatus status, Instant checkInTime) {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(RESERVATION_ID);
        reservation.setCode(RESERVATION_CODE);
        reservation.setCustomerEntity(customer);
        reservation.setStatus(status);
        reservation.setExpectedCheckIn(Timestamp.from(checkInTime));
        reservation.setExpectedCheckOut(Timestamp.from(checkInTime.plus(2, ChronoUnit.DAYS)));
        reservation.setTotalDeposit(DEPOSIT_AMOUNT);
        reservation.setNumberOfMembers(2);
        reservation.setCreatedAt(Timestamp.from(Instant.now()));
        reservation.setIsActive(true);
        return reservation;
    }
}
