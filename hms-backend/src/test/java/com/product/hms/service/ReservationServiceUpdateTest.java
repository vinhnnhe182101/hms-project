package com.product.hms.service;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.request.CustomerRequest;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.request.RoomClassQuantityRequest;
import com.product.hms.dto.response.CustomerResponse;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomAllocationEntity;
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
import org.mockito.InjectMocks;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService Update Tests")
class ReservationServiceUpdateTest {

    private static final Long RESERVATION_ID = 10L;
    private static final Long CUSTOMER_ID = 2L;
    private static final Long ROOM_CLASS_ID = 1L;

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

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private CustomerEntity customer;
    private CustomerResponse customerResponse;
    private RoomClassEntity roomClass;

    @BeforeEach
    void setUp() {
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

        roomClass = new RoomClassEntity();
        roomClass.setId(ROOM_CLASS_ID);
        roomClass.setName("Standard");
        roomClass.setBasePrice(BigDecimal.valueOf(100));
        roomClass.setStandardCapacity(2);
        roomClass.setMaxCapacity(4);
        roomClass.setExtraPersonFee(BigDecimal.valueOf(20));
        roomClass.setIsActive(true);
    }

    @Test
    @DisplayName("Should update reservation and recreate allocations when valid")
    void shouldUpdateReservationSuccessfully() {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(RESERVATION_ID);
        reservation.setCode("RS260307AAAAAA");
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setExpectedCheckIn(Timestamp.from(Instant.now().plus(3, ChronoUnit.DAYS)));
        reservation.setExpectedCheckOut(Timestamp.from(Instant.now().plus(5, ChronoUnit.DAYS)));
        reservation.setCreatedAt(Timestamp.from(Instant.now()));
        reservation.setIsActive(true);

        Timestamp newCheckIn = Timestamp.from(Instant.now().plus(6, ChronoUnit.DAYS));
        Timestamp newCheckOut = Timestamp.from(Instant.now().plus(8, ChronoUnit.DAYS));

        ReservationRequest request = new ReservationRequest(
                new CustomerRequest(CUSTOMER_ID, null, null, null, null),
                List.of(new RoomClassQuantityRequest(ROOM_CLASS_ID, 3)),
                newCheckIn,
                newCheckOut,
                3,
                "Updated note"
        );

        ReservationRoomAllocationEntity allocation = new ReservationRoomAllocationEntity();
        allocation.setId(100L);
        allocation.setReservationEntity(reservation);
        allocation.setRoomClassEntity(roomClass);
        allocation.setNumberOfPeople(3);

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(roomClassRepository.findById(ROOM_CLASS_ID)).thenReturn(Optional.of(roomClass));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(roomAllocationService.createRoomAllocations(any(), any(), any())).thenReturn(List.of(allocation));
        when(roomAllocationService.getAllocationsByReservation(any())).thenReturn(List.of(allocation));
        when(customerMapper.toResponse(customer)).thenReturn(customerResponse);

        ReservationResponse response = reservationService.updateReservation(RESERVATION_ID, request);

        assertThat(response).isNotNull();
        assertThat(response.bookingId()).isEqualTo(RESERVATION_ID);
        assertThat(response.checkInDate()).isEqualTo(newCheckIn);
        assertThat(response.checkOutDate()).isEqualTo(newCheckOut);
        assertThat(response.numberOfMembers()).isEqualTo(3);
        assertThat(response.note()).isEqualTo("Updated note");
        assertThat(response.allocations()).hasSize(1);
        assertThat(response.allocations().getFirst().numberOfPeople()).isEqualTo(3);

        verify(roomAllocationService).deleteAllocationsByReservation(reservation);
        verify(folioService).createFolioWithDepositItem(eq(allocation), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Should reject update when check-in is within 24 hours")
    void shouldRejectUpdateWithin24Hours() {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(RESERVATION_ID);
        reservation.setExpectedCheckIn(Timestamp.from(Instant.now().plus(10, ChronoUnit.HOURS)));

        ReservationRequest request = new ReservationRequest(
                new CustomerRequest(CUSTOMER_ID, null, null, null, null),
                List.of(new RoomClassQuantityRequest(ROOM_CLASS_ID, 2)),
                Timestamp.from(Instant.now().plus(2, ChronoUnit.DAYS)),
                Timestamp.from(Instant.now().plus(3, ChronoUnit.DAYS)),
                2,
                null
        );

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.updateReservation(RESERVATION_ID, request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_UPDATE_LOCKED);

        verify(reservationRepository, never()).save(any());
        verify(roomAllocationService, never()).deleteAllocationsByReservation(any());
    }

    @Test
    @DisplayName("Should throw not found when reservation does not exist")
    void shouldThrowWhenReservationNotFound() {
        ReservationRequest request = new ReservationRequest(
                new CustomerRequest(CUSTOMER_ID, null, null, null, null),
                List.of(new RoomClassQuantityRequest(ROOM_CLASS_ID, 2)),
                Timestamp.from(Instant.now().plus(2, ChronoUnit.DAYS)),
                Timestamp.from(Instant.now().plus(3, ChronoUnit.DAYS)),
                2,
                null
        );

        when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.updateReservation(RESERVATION_ID, request))
                .isInstanceOf(NotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);
    }
}
