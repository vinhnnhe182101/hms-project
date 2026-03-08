package com.product.hms.service;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.request.CustomerRequest;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.request.RoomClassQuantityRequest;
import com.product.hms.dto.response.CustomerResponse;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.*;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationServiceImpl
 * <p>
 * Test coverage:
 * - Happy path: create reservation with new customer
 * - Happy path: create reservation with existing customer
 * - Validation errors: null request, null customerRequest, empty roomClassQuantities
 * - Customer not found exception
 * - Room class not found exception
 * - Room class inactive exception
 * - Insufficient available rooms exception
 * - Multiple room classes booking
 * - Duplicate room class handling (merge quantity)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService Unit Tests")
class ReservationServiceTest {

    // ===== Constants =====
    private static final long CUSTOMER_ID = 1L;
    private static final long ROOM_CLASS_1_ID = 1L;
    private static final long ROOM_CLASS_2_ID = 2L;
    private static final long INVALID_CUSTOMER_ID = 999L;
    private static final long INVALID_ROOM_CLASS_ID = 999L;
    private static final String CUSTOMER_NAME = "John Doe";
    private static final String CUSTOMER_PHONE = "0123456789";
    private static final String CUSTOMER_IDENTITY_CARD = "123456789012";
    private static final String CUSTOMER_EMAIL = "john@example.com";
    private static final String CUSTOMER_TYPE = "ADULT";
    private static final String ROOM_CLASS_1_NAME = "Standard";
    private static final BigDecimal ROOM_CLASS_1_PRICE = BigDecimal.valueOf(100);
    private static final String ROOM_CLASS_2_NAME = "Deluxe";
    private static final BigDecimal ROOM_CLASS_2_PRICE = BigDecimal.valueOf(200);
    private static final String RESERVATION_CODE = "RS260305ABC123";
    private CustomerEntity mockCustomer;
    private CustomerRequest mockCustomerRequest;
    private CustomerResponse mockCustomerResponse;
    private RoomClassEntity mockRoomClass1;
    private RoomClassEntity mockRoomClass2;
    private ReservationEntity mockReservation;
    private Timestamp checkInDate;
    private Timestamp checkOutDate;

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
    private ReservationRoomAllocationRepository reservationRoomAllocationRepository;

    @Mock
    private FolioRepository folioRepository;

    @Mock
    private FolioItemRepository folioItemRepository;

    @Mock
    private CustomerMapper customerMapper;

    private ReservationServiceImpl reservationService;

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

        // Setup timestamps
        checkInDate = Timestamp.from(Instant.now().plus(1, ChronoUnit.DAYS));
        checkOutDate = Timestamp.from(Instant.now().plus(3, ChronoUnit.DAYS));

        mockCustomer = createMockCustomer();
        mockCustomerRequest = createMockCustomerRequest(null);
        mockCustomerResponse = createMockCustomerResponse();
        mockRoomClass1 = createMockRoomClass(ROOM_CLASS_1_ID, ROOM_CLASS_1_NAME, ROOM_CLASS_1_PRICE);
        mockRoomClass2 = createMockRoomClass(ROOM_CLASS_2_ID, ROOM_CLASS_2_NAME, ROOM_CLASS_2_PRICE);
        mockReservation = createMockReservation();
    }

    // ===== Helper Methods =====

    /**
     * Creates a mock customer entity with default values
     */
    private CustomerEntity createMockCustomer() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(CUSTOMER_ID);
        customer.setFullName(CUSTOMER_NAME);
        customer.setPhoneNumber(CUSTOMER_PHONE);
        customer.setIdentityCard(CUSTOMER_IDENTITY_CARD);
        customer.setEmail(CUSTOMER_EMAIL);
        customer.setType(CUSTOMER_TYPE);
        customer.setIsActive(true);
        return customer;
    }

    /**
     * Creates a mock customer request
     */
    private CustomerRequest createMockCustomerRequest(Long customerId) {
        return new CustomerRequest(
                customerId,
                CUSTOMER_IDENTITY_CARD,
                CUSTOMER_NAME,
                CUSTOMER_PHONE,
                CUSTOMER_EMAIL
        );
    }

    /**
     * Creates a mock customer response
     */
    private CustomerResponse createMockCustomerResponse() {
        return new CustomerResponse(
                CUSTOMER_ID,
                CUSTOMER_NAME,
                CUSTOMER_PHONE,
                CUSTOMER_IDENTITY_CARD,
                CUSTOMER_EMAIL,
                CUSTOMER_TYPE
        );
    }

    /**
     * Creates a mock room class entity
     */
    private RoomClassEntity createMockRoomClass(long id, String name, BigDecimal basePrice) {
        RoomClassEntity roomClass = new RoomClassEntity();
        roomClass.setId(id);
        roomClass.setName(name);
        roomClass.setBasePrice(basePrice);
        roomClass.setStandardCapacity(2);
        roomClass.setMaxCapacity(4);
        roomClass.setExtraPersonFee(BigDecimal.valueOf(20));
        roomClass.setIsActive(true);
        return roomClass;
    }

    /**
     * Creates a mock inactive room class entity
     */
    private RoomClassEntity createInactiveRoomClass() {
        RoomClassEntity roomClass = createMockRoomClass(ROOM_CLASS_1_ID, ROOM_CLASS_1_NAME, ROOM_CLASS_1_PRICE);
        roomClass.setIsActive(false);
        return roomClass;
    }

    /**
     * Creates a mock reservation entity
     */
    private ReservationEntity createMockReservation() {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(CUSTOMER_ID);
        reservation.setCode(RESERVATION_CODE);
        reservation.setCustomerEntity(mockCustomer);
        reservation.setExpectedCheckIn(checkInDate);
        reservation.setExpectedCheckOut(checkOutDate);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setTotalDeposit(BigDecimal.ZERO);
        reservation.setNumberOfMembers(2);
        reservation.setNote("Test booking");
        reservation.setCreatedAt(Timestamp.from(Instant.now()));
        reservation.setIsActive(true);
        return reservation;
    }

    /**
     * Creates a RoomClassQuantityRequest for a single room class with number of people
     */
    private RoomClassQuantityRequest createRoomClassQuantity(long roomClassId, int numberOfPeople) {
        return new RoomClassQuantityRequest(roomClassId, numberOfPeople);
    }

    /**
     * Creates a basic ReservationRequest
     */
    private ReservationRequest createReservationRequest(CustomerRequest customerReq,
                                                        List<RoomClassQuantityRequest> quantities,
                                                        Integer numberOfMembers,
                                                        String note) {
        return new ReservationRequest(
                customerReq,
                quantities,
                checkInDate,
                checkOutDate,
                numberOfMembers,
                note
        );
    }

    /**
     * Sets up mocks for successful customer resolution with new customer
     */
    private void setupMocksForNewCustomer() {
        when(customerMapper.toEntity(mockCustomerRequest)).thenReturn(mockCustomer);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(mockCustomer);
    }

    /**
     * Sets up mocks for successful existing customer resolution
     */
    private void setupMocksForExistingCustomer() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(mockCustomer));
    }

    /**
     * Sets up mocks for room class retrieval
     */
    private void setupMocksForRoomClass(long roomClassId, RoomClassEntity roomClass) {
        when(roomClassRepository.findById(roomClassId)).thenReturn(Optional.of(roomClass));
    }

    /**
     * Sets up mocks for successful reservation creation
     */
    private void setupMocksForReservationCreation() {
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(mockReservation);

        ReservationRoomEntity mockAllocation = new ReservationRoomEntity();
        mockAllocation.setId(1L);
        mockAllocation.setRoomClassEntity(mockRoomClass1);
        mockAllocation.setNumberOfPeople(2);

        when(roomAllocationService.createRoomAllocations(any(), any(), any()))
                .thenReturn(List.of(mockAllocation));
        when(roomAllocationService.getAllocationsByReservation(any()))
                .thenReturn(List.of(mockAllocation));
        when(customerMapper.toResponse(mockCustomer)).thenReturn(mockCustomerResponse);
    }

    /**
     * Verifies basic repository interactions for new customer flow
     */
    private void verifyNewCustomerFlow() {
        verify(customerMapper).toEntity(mockCustomerRequest);
        verify(customerRepository).save(any(CustomerEntity.class));
    }

    /**
     * Verifies customer mapper is not called for existing customer
     */
    private void verifyExistingCustomerFlow() {
        verify(customerRepository).findById(CUSTOMER_ID);
        verify(customerRepository, never()).save(any(CustomerEntity.class));
        verify(customerMapper, never()).toEntity(any(CustomerRequest.class));
    }

    /**
     * Verifies common repository calls for successful reservation
     */
    private void verifySuccessfulReservationCreation() {
        verify(reservationRepository).save(any(ReservationEntity.class));
        verify(roomAllocationService).createRoomAllocations(any(), any(), any());
        verify(folioService).createFolioWithDepositItem(any(ReservationRoomEntity.class), any(BigDecimal.class));
        verify(customerMapper).toResponse(mockCustomer);
    }

    @Test
    @DisplayName("Should create reservation successfully with new customer")
    void testCreateReservation_WithNewCustomer_Success() {
        // Given
        List<RoomClassQuantityRequest> roomClassQuantities = List.of(createRoomClassQuantity(ROOM_CLASS_1_ID, 2));
        ReservationRequest request = createReservationRequest(mockCustomerRequest, roomClassQuantities, 2, "Test booking");

        setupMocksForNewCustomer();
        setupMocksForRoomClass(ROOM_CLASS_1_ID, mockRoomClass1);
        setupMocksForReservationCreation();

        // When
        ReservationResponse response = reservationService.createReservation(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.bookingId()).isEqualTo(CUSTOMER_ID);
        assertThat(response.bookingCode()).isEqualTo(RESERVATION_CODE);
        assertThat(response.customer()).isEqualTo(mockCustomerResponse);
        assertThat(response.allocations()).hasSize(1);
        assertThat(response.allocations().getFirst().roomClassId()).isEqualTo(ROOM_CLASS_1_ID);
        assertThat(response.allocations().getFirst().numberOfPeople()).isEqualTo(2);
        assertThat(response.status()).isEqualTo("CONFIRMED");
        assertThat(response.numberOfMembers()).isEqualTo(2);

        // Verify
        verifyNewCustomerFlow();
        verifySuccessfulReservationCreation();
    }

    @Test
    @DisplayName("Should create reservation successfully with existing customer")
    void testCreateReservation_WithExistingCustomer_Success() {
        // Given
        CustomerRequest customerRequestWithId = createMockCustomerRequest(CUSTOMER_ID);
        List<RoomClassQuantityRequest> roomClassQuantities = List.of(createRoomClassQuantity(ROOM_CLASS_1_ID, 2));
        ReservationRequest request = createReservationRequest(customerRequestWithId, roomClassQuantities, 1, null);

        setupMocksForExistingCustomer();
        setupMocksForRoomClass(ROOM_CLASS_1_ID, mockRoomClass1);
        setupMocksForReservationCreation();

        // When
        ReservationResponse response = reservationService.createReservation(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.customer()).isEqualTo(mockCustomerResponse);

        verifyExistingCustomerFlow();
        verifySuccessfulReservationCreation();
    }

    @Test
    @DisplayName("Should throw BadRequestException when request is null")
    void testCreateReservation_NullRequest_ThrowsBadRequestException() {
        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("request must be provided")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REQUEST);

        verify(customerRepository, never()).save(any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when customerRequest is null")
    void testCreateReservation_NullCustomerRequest_ThrowsBadRequestException() {
        // Given
        ReservationRequest request = new ReservationRequest(
                null,
                List.of(createRoomClassQuantity(ROOM_CLASS_1_ID, 2)),
                checkInDate,
                checkOutDate,
                1,
                null
        );

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("customerRequest must be provided")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("Should throw BadRequestException when roomClassQuantities is empty")
    void testCreateReservation_EmptyRoomClassQuantities_ThrowsBadRequestException() {
        // Given
        ReservationRequest request = new ReservationRequest(
                mockCustomerRequest,
                Collections.emptyList(),
                checkInDate,
                checkOutDate,
                1,
                null
        );

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("roomClassQuantities must not be empty")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("Should throw NotFoundException when customer not found")
    void testCreateReservation_CustomerNotFound_ThrowsNotFoundException() {
        // Given
        CustomerRequest customerRequestWithId = createMockCustomerRequest(INVALID_CUSTOMER_ID);
        ReservationRequest request = createReservationRequest(customerRequestWithId,
                List.of(createRoomClassQuantity(ROOM_CLASS_1_ID, 2)), 1, null);

        when(customerRepository.findById(INVALID_CUSTOMER_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found with ID: " + INVALID_CUSTOMER_ID)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.CUSTOMER_NOT_FOUND);

        verify(customerRepository).findById(INVALID_CUSTOMER_ID);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when room class not found")
    void testCreateReservation_RoomClassNotFound_ThrowsNotFoundException() {
        // Given
        List<RoomClassQuantityRequest> roomClassQuantities = List.of(createRoomClassQuantity(INVALID_ROOM_CLASS_ID, 2));
        ReservationRequest request = createReservationRequest(mockCustomerRequest, roomClassQuantities, 1, null);

        setupMocksForNewCustomer();
        when(roomClassRepository.findById(INVALID_ROOM_CLASS_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Room class not found with ID: " + INVALID_ROOM_CLASS_ID)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ROOM_CLASS_NOT_FOUND);

        verify(roomClassRepository).findById(INVALID_ROOM_CLASS_ID);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when room class is inactive")
    void testCreateReservation_RoomClassInactive_ThrowsBusinessException() {
        // Given
        RoomClassEntity inactiveRoomClass = createInactiveRoomClass();
        ReservationRequest request = createReservationRequest(
                mockCustomerRequest,
                List.of(createRoomClassQuantity(ROOM_CLASS_1_ID, 2)),
                1,
                null
        );

        setupMocksForNewCustomer();
        setupMocksForRoomClass(ROOM_CLASS_1_ID, inactiveRoomClass);

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Room class is not active: " + ROOM_CLASS_1_ID)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ROOM_CLASS_INACTIVE);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should allow large number of people when equals max capacity")
    void testCreateReservation_LargePeopleCount_Success() {
        // Given - maxCapacity is 4, request 4 people (valid)
        ReservationRequest request = createReservationRequest(
                mockCustomerRequest,
                List.of(createRoomClassQuantity(ROOM_CLASS_1_ID, 4)),
                4,
                null
        );

        setupMocksForNewCustomer();
        setupMocksForRoomClass(ROOM_CLASS_1_ID, mockRoomClass1);
        setupMocksForReservationCreation();

        // When
        ReservationResponse response = reservationService.createReservation(request);

        // Then
        assertThat(response).isNotNull();
        verifySuccessfulReservationCreation();
    }

    @Test
    @DisplayName("Should create reservation with multiple room classes")
    void testCreateReservation_MultipleRoomClasses_Success() {
        // Given
        List<RoomClassQuantityRequest> roomClassQuantities = List.of(
                createRoomClassQuantity(ROOM_CLASS_1_ID, 2),
                createRoomClassQuantity(ROOM_CLASS_2_ID, 3)
        );
        ReservationRequest request = createReservationRequest(mockCustomerRequest, roomClassQuantities, 3, "Multiple room classes");

        ReservationRoomEntity mockAllocation1 = new ReservationRoomEntity();
        mockAllocation1.setId(1L);
        mockAllocation1.setRoomClassEntity(mockRoomClass1);
        mockAllocation1.setNumberOfPeople(2);

        ReservationRoomEntity mockAllocation2 = new ReservationRoomEntity();
        mockAllocation2.setId(2L);
        mockAllocation2.setRoomClassEntity(mockRoomClass2);
        mockAllocation2.setNumberOfPeople(3);

        setupMocksForNewCustomer();
        setupMocksForRoomClass(ROOM_CLASS_1_ID, mockRoomClass1);
        setupMocksForRoomClass(ROOM_CLASS_2_ID, mockRoomClass2);
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(mockReservation);
        when(roomAllocationService.createRoomAllocations(any(), any(), any()))
                .thenReturn(List.of(mockAllocation1, mockAllocation2));
        when(roomAllocationService.getAllocationsByReservation(any()))
                .thenReturn(List.of(mockAllocation1, mockAllocation2));
        when(customerMapper.toResponse(mockCustomer)).thenReturn(mockCustomerResponse);

        // When
        ReservationResponse response = reservationService.createReservation(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.allocations()).hasSize(2);
        assertThat(response.allocations().get(0).roomClassId()).isEqualTo(ROOM_CLASS_1_ID);
        assertThat(response.allocations().get(0).numberOfPeople()).isEqualTo(2);
        assertThat(response.allocations().get(1).roomClassId()).isEqualTo(ROOM_CLASS_2_ID);
        assertThat(response.allocations().get(1).numberOfPeople()).isEqualTo(3);

        verify(roomAllocationService, times(1)).createRoomAllocations(any(), any(), any());
    }

    @Test
    @DisplayName("Should keep separate allocations when same room class is added multiple times")
    void testCreateReservation_DuplicateRoomClass_KeepsSeparate() {
        // Given
        List<RoomClassQuantityRequest> roomClassQuantities = List.of(
                createRoomClassQuantity(ROOM_CLASS_1_ID, 2),
                createRoomClassQuantity(ROOM_CLASS_1_ID, 3)
        );
        ReservationRequest request = createReservationRequest(mockCustomerRequest, roomClassQuantities, 2, "Duplicate room class test");

        ReservationRoomEntity mockAllocation1 = new ReservationRoomEntity();
        mockAllocation1.setId(1L);
        mockAllocation1.setRoomClassEntity(mockRoomClass1);
        mockAllocation1.setNumberOfPeople(2);

        ReservationRoomEntity mockAllocation2 = new ReservationRoomEntity();
        mockAllocation2.setId(2L);
        mockAllocation2.setRoomClassEntity(mockRoomClass1);
        mockAllocation2.setNumberOfPeople(3);

        setupMocksForNewCustomer();
        setupMocksForRoomClass(ROOM_CLASS_1_ID, mockRoomClass1);
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(mockReservation);
        when(roomAllocationService.createRoomAllocations(any(), any(), any()))
                .thenReturn(List.of(mockAllocation1, mockAllocation2));
        when(roomAllocationService.getAllocationsByReservation(any()))
                .thenReturn(List.of(mockAllocation1, mockAllocation2));
        when(customerMapper.toResponse(mockCustomer)).thenReturn(mockCustomerResponse);

        // When
        ReservationResponse response = reservationService.createReservation(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.allocations()).hasSize(2);
        assertThat(response.allocations().get(0).roomClassId()).isEqualTo(ROOM_CLASS_1_ID);
        assertThat(response.allocations().get(0).numberOfPeople()).isEqualTo(2);
        assertThat(response.allocations().get(1).roomClassId()).isEqualTo(ROOM_CLASS_1_ID);
        assertThat(response.allocations().get(1).numberOfPeople()).isEqualTo(3);

        verify(roomAllocationService, times(1)).createRoomAllocations(any(), any(), any());
    }

    @Test
    @DisplayName("Should set default numberOfMembers to 1 when not provided")
    void testCreateReservation_NoNumberOfMembers_DefaultsToOne() {
        // Given
        List<RoomClassQuantityRequest> roomClassQuantities = List.of(createRoomClassQuantity(ROOM_CLASS_1_ID, 2));
        ReservationRequest request = createReservationRequest(mockCustomerRequest, roomClassQuantities, null, null);

        setupMocksForNewCustomer();
        setupMocksForRoomClass(ROOM_CLASS_1_ID, mockRoomClass1);
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(invocation -> {
            ReservationEntity savedReservation = invocation.getArgument(0);
            assertThat(savedReservation.getNumberOfMembers()).isEqualTo(1);
            return mockReservation;
        });
        when(roomAllocationService.createRoomAllocations(any(), any(), any()))
                .thenReturn(List.of(new ReservationRoomEntity()));
        when(customerMapper.toResponse(mockCustomer)).thenReturn(mockCustomerResponse);

        // When
        ReservationResponse response = reservationService.createReservation(request);

        // Then
        assertThat(response).isNotNull();
        verify(reservationRepository).save(argThat(reservation -> reservation.getNumberOfMembers() == 1));
    }

    @Test
    @DisplayName("Should capture and save reservation with correct values including deposit")
    void testCreateReservation_SavesCorrectReservationValues() {
        // Given
        List<RoomClassQuantityRequest> roomClassQuantities = List.of(createRoomClassQuantity(ROOM_CLASS_1_ID, 2));
        ReservationRequest request = createReservationRequest(mockCustomerRequest, roomClassQuantities, 3, "Special request");

        ArgumentCaptor<ReservationEntity> reservationCaptor = ArgumentCaptor.forClass(ReservationEntity.class);

        setupMocksForNewCustomer();
        setupMocksForRoomClass(ROOM_CLASS_1_ID, mockRoomClass1);
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(mockReservation);
        when(roomAllocationService.createRoomAllocations(any(), any(), any()))
                .thenReturn(List.of(new ReservationRoomEntity()));
        when(customerMapper.toResponse(mockCustomer)).thenReturn(mockCustomerResponse);

        // When
        reservationService.createReservation(request);

        // Then
        verify(reservationRepository).save(reservationCaptor.capture());
        ReservationEntity savedReservation = reservationCaptor.getValue();

        assertThat(savedReservation.getCustomerEntity()).isEqualTo(mockCustomer);
        assertThat(savedReservation.getExpectedCheckIn()).isEqualTo(checkInDate);
        assertThat(savedReservation.getExpectedCheckOut()).isEqualTo(checkOutDate);
        assertThat(savedReservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        // Total cost = 100 (base price), deposit = 100 * 0.2 = 20
        assertThat(savedReservation.getTotalDeposit()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
        assertThat(savedReservation.getNumberOfMembers()).isEqualTo(3);
        assertThat(savedReservation.getNote()).isEqualTo("Special request");
        assertThat(savedReservation.getIsActive()).isTrue();
        assertThat(savedReservation.getCode()).isNotNull();
        assertThat(savedReservation.getCreatedAt()).isNotNull();

        // Verify folio creation was delegated to folio service
        verify(folioService).createFolioWithDepositItem(any(ReservationRoomEntity.class), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when number of people exceeds max capacity")
    void testCreateReservation_ExceedMaxCapacity_ThrowsBadRequestException() {
        // Given - maxCapacity is 4, request 5 people
        ReservationRequest request = createReservationRequest(
                mockCustomerRequest,
                List.of(createRoomClassQuantity(ROOM_CLASS_1_ID, 5)),
                5,
                null
        );

        setupMocksForNewCustomer();
        setupMocksForRoomClass(ROOM_CLASS_1_ID, mockRoomClass1);

        // When & Then
        assertThatThrownBy(() -> reservationService.createReservation(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("exceeds max capacity")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EXCEED_MAX_CAPACITY);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should calculate deposit with extra person fee when exceeds standard capacity")
    void testCreateReservation_CalculatesExtraPersonFee_Success() {
        // Given - standardCapacity is 2, request 3 people (1 extra person)
        List<RoomClassQuantityRequest> roomClassQuantities = List.of(createRoomClassQuantity(ROOM_CLASS_1_ID, 3));
        ReservationRequest request = createReservationRequest(mockCustomerRequest, roomClassQuantities, 3, null);

        ArgumentCaptor<ReservationEntity> reservationCaptor = ArgumentCaptor.forClass(ReservationEntity.class);

        setupMocksForNewCustomer();
        setupMocksForRoomClass(ROOM_CLASS_1_ID, mockRoomClass1);
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(mockReservation);
        when(roomAllocationService.createRoomAllocations(any(), any(), any()))
                .thenReturn(List.of(new ReservationRoomEntity()));
        when(customerMapper.toResponse(mockCustomer)).thenReturn(mockCustomerResponse);

        // When
        reservationService.createReservation(request);

        // Then
        verify(reservationRepository).save(reservationCaptor.capture());
        ReservationEntity savedReservation = reservationCaptor.getValue();

        // Total cost = 100 (base) + 20 (1 extra person) = 120, deposit = 120 * 0.2 = 24
        assertThat(savedReservation.getTotalDeposit()).isEqualByComparingTo(BigDecimal.valueOf(24.00));

        verify(folioService).createFolioWithDepositItem(any(ReservationRoomEntity.class), any(BigDecimal.class));
    }
}

