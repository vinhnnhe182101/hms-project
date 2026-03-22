package com.product.hms.service.impl;

import com.product.hms.constants.Reservation;
import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.request.BookingRequestDTO;
import com.product.hms.dto.request.ReservationCheckInRequest;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.request.ReservationSearchRequest;
import com.product.hms.dto.response.BookingResponseDTO;
import com.product.hms.dto.response.CustomerResponse;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.dto.response.RoomClassQuantityResponse;
import com.product.hms.entity.*;
import com.product.hms.enums.ReservationRoomStatus;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.*;
import com.product.hms.service.FolioService;
import com.product.hms.service.PaymentService;
import com.product.hms.service.ReservationRoomService;
import com.product.hms.service.ReservationService;
import com.product.hms.service.impl.reservation.ReservationCheckInSupport;
import com.product.hms.service.impl.reservation.ReservationPricingSupport;
import com.product.hms.service.impl.reservation.ReservationResponseSupport;
import com.product.hms.service.impl.reservation.ReservationValidationSupport;
import com.product.hms.utils.FieldNameUtils;
import com.product.hms.utils.RandomUtils;
import com.product.hms.utils.specification.PageSpecificationUtils;
import com.product.hms.utils.specification.SpecificationUtils;
import com.product.hms.utils.specification.search.SearchCriteria;
import com.product.hms.utils.specification.sort.SortCriteria;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.product.hms.utils.specification.search.SearchCriteria.ComparisonOperator.*;
import static com.product.hms.utils.specification.sort.SortCriteria.SortDirection.DESC;

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
    private final ReservationRoomService reservationRoomService;
    private final FolioService folioService;
    private final PaymentService paymentService;
    private final CustomerMapper customerMapper;
    private final SpecificationUtils<ReservationEntity> specificationUtils;
    private final PageSpecificationUtils<ReservationEntity> pageSpecificationUtils;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        ReservationValidationSupport.validateCreateReservationRequest(request);

        CustomerEntity customer = ReservationValidationSupport.resolveCustomer(request, customerRepository,
                customerMapper);
        Map<Long, RoomClassEntity> roomClassById = ReservationValidationSupport.loadAndValidateRoomClasses(
                request,
                roomClassRepository);

        BigDecimal depositAmount = ReservationPricingSupport.calculateDepositForRequest(request, roomClassById);
        ReservationEntity reservation = saveReservation(request, customer, depositAmount);

        createAllocationsAndFolios(reservation, request, depositAmount);
        return ReservationResponseSupport.buildReservationResponse(reservation, customer, reservationRoomService,
                customerMapper);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(Long reservationId, ReservationRequest request) {
        ReservationValidationSupport.validateCreateReservationRequest(request);

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found with ID: " + reservationId));

        ReservationValidationSupport.validateUpdateWindow(reservation);
        CustomerEntity customer = ReservationValidationSupport.resolveCustomer(request, customerRepository,
                customerMapper);
        Map<Long, RoomClassEntity> roomClassById = ReservationValidationSupport.loadAndValidateRoomClasses(
                request,
                roomClassRepository);

        BigDecimal depositAmount = ReservationPricingSupport.calculateDepositForRequest(request, roomClassById);
        updateReservationFields(reservation, request, customer, depositAmount);
        reservationRepository.save(reservation);

        reservationRoomService.deleteAllocationsByReservation(reservation);
        createAllocationsAndFolios(reservation, request, depositAmount);

        return ReservationResponseSupport.buildReservationResponse(reservation, customer, reservationRoomService,
                customerMapper);
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found with ID: " + reservationId));

        ReservationValidationSupport.validateCancellationAllowed(reservation);
        boolean isEligibleForRefund = ReservationValidationSupport.isRefundEligible(reservation);

        List<ReservationRoomEntity> reservationRoomEntities = reservationRoomService
                .getAllocationsByReservation(reservation);
        for (ReservationRoomEntity reservationRoomEntity : reservationRoomEntities) {
            if (isEligibleForRefund) {
                folioService.createRefundItem(reservationRoomEntity, reservation.getTotalDeposit());
            } else {
                folioService.createCancellationFeeItem(reservationRoomEntity, reservation.getTotalDeposit());
            }
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        return ReservationResponseSupport.buildReservationResponse(
                reservation,
                reservation.getCustomerEntity(),
                reservationRoomService,
                customerMapper);
    }

    @Override
    @Transactional
    public ReservationResponse checkInReservation(Long reservationId, ReservationCheckInRequest request) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found with ID: " + reservationId));

        ReservationValidationSupport.validateCheckInRequest(request);
        ReservationValidationSupport.validateCheckInAllowed(reservation);

        ReservationCheckInSupport.assignRoomsForCheckIn(
                reservationId,
                request,
                reservationRoomRepository,
                roomRepository);

        applyEarlyCheckInFees(reservation);

        reservation.setStatus(ReservationStatus.IN_HOUSE);
        reservationRepository.save(reservation);

        return ReservationResponseSupport.buildReservationResponse(
                reservation,
                reservation.getCustomerEntity(),
                reservationRoomService,
                customerMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservationResponse> search(ReservationSearchRequest filter, Pageable pageable) {
        List<SearchCriteria> searchCriteria = new ArrayList<>();
        searchCriteria.add(new SearchCriteria(
                FieldNameUtils.joinFields(
                        ReservationEntity.Fields.customerEntity,
                        CustomerEntity.Fields.fullName
                )
                , CONTAINS, filter.guestName()));
        searchCriteria.add(new SearchCriteria(
                FieldNameUtils.joinFields(
                        ReservationEntity.Fields.customerEntity,
                        CustomerEntity.Fields.identityCard
                )
                , CONTAINS, filter.identityCard()));
        searchCriteria.add(new SearchCriteria("status", EQUALS, filter.status()));
        searchCriteria.add(new SearchCriteria(ReservationEntity.Fields.expectedCheckIn, GREATER_THAN_OR_EQUAL_TO, filter.checkInDateFrom()));
        searchCriteria.add(new SearchCriteria(ReservationEntity.Fields.expectedCheckOut, LESS_THAN_OR_EQUAL_TO, filter.checkInDateTo()));
        List<SortCriteria> sortCriteria = new ArrayList<>();
        if (pageable.getSort().isEmpty()) {
            sortCriteria.add(new SortCriteria(ReservationEntity.Fields.expectedCheckIn, SortCriteria.AggregationFunction.NONE, DESC, JoinType.LEFT));
        }
        var spec = specificationUtils
                .reset()
                .getSpecifications(searchCriteria, sortCriteria);
        return pageSpecificationUtils.getPage(spec, pageable, ReservationEntity.class)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getById(Long id) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.RESERVATION_NOT_FOUND,
                        "Reservation not found with ID: " + id));
        
        return ReservationResponseSupport.buildReservationResponse(
                reservation,
                reservation.getCustomerEntity(),
                reservationRoomService,
                customerMapper);
    }

    private ReservationResponse toResponse(ReservationEntity entity) {
        return ReservationResponseSupport.buildReservationResponse(
                entity,
                entity.getCustomerEntity(),
                reservationRoomService,
                customerMapper);
    }

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        CustomerEntity customer;

        if (request.getCustomer().getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomer().getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng"));

            if (request.getCustomer().getName() != null && !request.getCustomer().getName().isBlank()) {
                customer.setFullName(request.getCustomer().getName());
            }
            if (request.getCustomer().getPhone() != null && !request.getCustomer().getPhone().isBlank()) {
                customer.setPhoneNumber(request.getCustomer().getPhone());
            }
            if (request.getCustomer().getIdentityCard() != null && !request.getCustomer().getIdentityCard().isBlank()) {
                customer.setIdentityCard(request.getCustomer().getIdentityCard());
            }
            customer = customerRepository.save(customer);

        } else {
            customer = customerRepository.findByIdentityCard(request.getCustomer().getIdentityCard())
                    .orElseGet(() -> customerRepository.findByPhoneNumber(request.getCustomer().getPhone())
                            .orElseGet(() -> {
                                CustomerEntity newCustomer = new CustomerEntity();
                                newCustomer.setFullName(request.getCustomer().getName());
                                newCustomer.setPhoneNumber(request.getCustomer().getPhone());
                                newCustomer.setIdentityCard(request.getCustomer().getIdentityCard());
                                newCustomer.setIsActive(true);
                                return customerRepository.save(newCustomer);
                            }));
        }


        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        Instant checkInInstant = request.getCheckIn();
        Instant checkOutInstant = request.getCheckOut();

        Instant now = Instant.now();
        ZonedDateTime nowZoned = now.atZone(zoneId);
        ZonedDateTime checkInZoned = checkInInstant.atZone(zoneId);
        ZonedDateTime checkOutZoned = checkOutInstant.atZone(zoneId);

        if (checkOutInstant.isBefore(checkInInstant) || checkOutInstant.equals(checkInInstant)) {
            throw new BadRequestException(ErrorCode.INVALID_DATE_RANGE, "Thời gian check-out phải sau thời gian check-in.");
        }

        // 1. Check-in must be at least 1 hour from now if it is today
        if (checkInZoned.toLocalDate().isEqual(nowZoned.toLocalDate())) {
            if (checkInInstant.isBefore(now.plus(1, ChronoUnit.HOURS))) {
                throw new BadRequestException(ErrorCode.INVALID_DATE_RANGE, "Thời gian check-in phải sau ít nhất 1 giờ kể từ hiện tại nếu bạn đặt vào hôm nay.");
            }
        } else if (checkInInstant.isBefore(now)) {
            throw new BadRequestException(ErrorCode.INVALID_DATE_RANGE, "Thời gian check-in không được ở trong quá khứ.");
        }

        // 2. Check-in and Check-out within 2 months
        ZonedDateTime maxLimit = nowZoned.plusMonths(2);
        if (checkInZoned.isAfter(maxLimit)) {
            throw new BadRequestException(ErrorCode.INVALID_DATE_RANGE, "Chỉ cho phép đặt phòng trong vòng 2 tháng tới.");
        }
        if (checkOutZoned.isAfter(maxLimit.plusDays(1))) {
            throw new BadRequestException(ErrorCode.INVALID_DATE_RANGE, "Thời gian trả phòng không được vượt quá 2 tháng kể từ hiện tại.");
        }

        // 3. Guest count validation against max room capacity
        int totalMaxCapacity = 0;
        if (request.getRooms() != null) {
            for (BookingRequestDTO.RoomBookingRequest r : request.getRooms()) {
                RoomClassEntity roomClass = roomClassRepository.findById(r.getId())
                        .orElseThrow(() -> new NotFoundException(ErrorCode.ROOM_CLASS_NOT_FOUND, "Không tìm thấy loại phòng ID: " + r.getId()));
                totalMaxCapacity += roomClass.getMaxCapacity() * r.getQuantity();
            }
        }
        if (request.getGuests() > totalMaxCapacity) {
            throw new BadRequestException(ErrorCode.EXCEED_MAX_CAPACITY, "Số lượng khách (" + request.getGuests() + ") vượt quá tổng sức chứa tối đa của các phòng đã chọn (" + totalMaxCapacity + " người).");
        }
        // --- VALIDATION END ---

        LocalDate checkInDate = checkInInstant.atZone(zoneId).toLocalDate();
        LocalDate checkOutDate = checkOutInstant.atZone(zoneId).toLocalDate();

        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights <= 0) nights = 1;

        // BƯỚC 1: Thu thập phòng khả dụng và thiết lập mảng phân bổ
        int totalRequestedRooms = request.getRooms() != null ? request.getRooms().stream().mapToInt(BookingRequestDTO.RoomBookingRequest::getQuantity).sum() : 0;
        if (request.getGuests() < totalRequestedRooms && request.getGuests() > 0) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Số lượng khách tối thiểu phải bằng số phòng đã chọn.");
        }

        List<RoomClassEntity> roomClasses = new ArrayList<>();
        List<RoomEntity> assignedRooms = new ArrayList<>();
        List<BigDecimal> pricesPerNight = new ArrayList<>();
        List<Integer> peoplePerRoom = new ArrayList<>();

        if (request.getRooms() != null) {
            for (BookingRequestDTO.RoomBookingRequest roomReq : request.getRooms()) {
                RoomClassEntity roomClass = roomClassRepository.findById(roomReq.getId())
                        .orElseThrow(() -> new RuntimeException("Room Class not found: " + roomReq.getId()));

                List<RoomEntity> availableRooms = roomRepository.findAvailableRoomsByClass(
                        roomReq.getId(),
                        Timestamp.from(checkInInstant),
                        Timestamp.from(checkOutInstant),
                        List.of(ReservationStatus.PENDING_DEPOSIT, ReservationStatus.CONFIRMED, ReservationStatus.IN_HOUSE)
                );

                if (availableRooms.size() < roomReq.getQuantity()) {
                    throw new RuntimeException("Không đủ phòng trống cho loại phòng: " + roomClass.getName());
                }

                Collections.shuffle(availableRooms);
                BigDecimal roomPricePerNight = roomReq.getPricePerNight() != null ? roomReq.getPricePerNight() : roomClass.getBasePrice();

                for (int i = 0; i < roomReq.getQuantity(); i++) {
                    roomClasses.add(roomClass);
                    assignedRooms.add(availableRooms.get(i));
                    pricesPerNight.add(roomPricePerNight);
                    peoplePerRoom.add(0);
                }
            }
        }

        // BƯỚC 2: Thuật toán phân bổ người vào phòng
        int remainingGuests = request.getGuests();
        int totalRooms = peoplePerRoom.size();

        // 2.1. Phân mỗi phòng 1 người trước
        for (int i = 0; i < totalRooms; i++) {
            if (remainingGuests > 0) {
                peoplePerRoom.set(i, 1);
                remainingGuests--;
            }
        }

        // 2.2. Phân tiếp cho đủ standard_capacity
        for (int i = 0; i < totalRooms; i++) {
            RoomClassEntity rc = roomClasses.get(i);
            int current = peoplePerRoom.get(i);
            int spaceToStandard = rc.getStandardCapacity() - current;
            if (spaceToStandard > 0 && remainingGuests > 0) {
                int add = Math.min(spaceToStandard, remainingGuests);
                peoplePerRoom.set(i, current + add);
                remainingGuests -= add;
            }
        }

        // 2.3. Phân tiếp cho đến max_capacity
        for (int i = 0; i < totalRooms; i++) {
            RoomClassEntity rc = roomClasses.get(i);
            int current = peoplePerRoom.get(i);
            int spaceToMax = rc.getMaxCapacity() - current;
            if (spaceToMax > 0 && remainingGuests > 0) {
                int add = Math.min(spaceToMax, remainingGuests);
                peoplePerRoom.set(i, current + add);
                remainingGuests -= add;
            }
        }

        // BƯỚC 3: Tính toán tổng tiền (có xét phụ thu)
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<BigDecimal> finalRoomCharges = new ArrayList<>();
        List<BigDecimal> finalPricePerNights = new ArrayList<>();

        for (int i = 0; i < totalRooms; i++) {
            RoomClassEntity rc = roomClasses.get(i);
            int count = peoplePerRoom.get(i);
            BigDecimal basePrice = pricesPerNight.get(i);

            int extraPeople = Math.max(0, count - rc.getStandardCapacity());
            BigDecimal extraFeePerNight = rc.getExtraPersonFee() != null ? rc.getExtraPersonFee() : BigDecimal.ZERO;
            
            BigDecimal finalPricePerNight = basePrice.add(extraFeePerNight.multiply(BigDecimal.valueOf(extraPeople)));
            BigDecimal roomTotalCharge = finalPricePerNight.multiply(BigDecimal.valueOf(nights));
            
            finalPricePerNights.add(finalPricePerNight);
            finalRoomCharges.add(roomTotalCharge);
            totalAmount = totalAmount.add(roomTotalCharge);
        }

        BigDecimal depositAmount = totalAmount.multiply(Reservation.DEPOSIT_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setCode("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.setCustomerEntity(customer);
        reservation.setExpectedCheckIn(Timestamp.from(checkInInstant));
        reservation.setExpectedCheckOut(Timestamp.from(checkOutInstant));
        reservation.setStatus(ReservationStatus.PENDING_DEPOSIT);
        reservation.setTotalDeposit(depositAmount);
        reservation.setNumberOfMembers(request.getGuests());
        reservation.setNote(request.getCustomer().getNote());
        reservation.setCreatedAt(Timestamp.valueOf(LocalDateTime.now(zoneId)));
        reservation.setIsActive(true);

        ReservationEntity savedReservation = reservationRepository.save(reservation);

        Long firstFolioId = null;

        // BƯỚC 4: Tạo ReservationRoomEntity và Folio
        for (int i = 0; i < totalRooms; i++) {
            ReservationRoomEntity reservationRoomEntity = new ReservationRoomEntity();
            reservationRoomEntity.setReservationEntity(savedReservation);
            reservationRoomEntity.setRoomClassEntity(roomClasses.get(i));
            reservationRoomEntity.setRoomEntity(assignedRooms.get(i));
            reservationRoomEntity.setPriceAtBooking(finalPricePerNights.get(i));
            reservationRoomEntity.setNumberOfPeople(peoplePerRoom.get(i));
            reservationRoomEntity.setStatus(ReservationRoomStatus.ASSIGNED);
            reservationRoomEntity.setIsActive(true);
            ReservationRoomEntity savedRoom = reservationRoomRepository.save(reservationRoomEntity);

            FolioEntity createdFolio = folioService.createFolioForBooking(savedRoom, finalRoomCharges.get(i));
            if (firstFolioId == null) {
                firstFolioId = createdFolio.getId();
            }
        }

        String paymentUrl = null;
        if (firstFolioId != null && depositAmount.signum() > 0) {
            // Lấy địa chỉ IP giả (hoặc bạn có thể bổ sung từ HttpServletRequest vào BookingRequestDTO)
            String clientIp = "127.0.0.1";
            paymentUrl = paymentService.createVnPayPaymentUrl(firstFolioId, depositAmount, clientIp);
        }

        return BookingResponseDTO.builder()
                .reservationId(savedReservation.getId())
                .reservationCode(savedReservation.getCode())
                .totalAmount(totalAmount)
                .depositAmount(depositAmount)
                .paymentUrl(paymentUrl)
                .build();
    }

    private void applyEarlyCheckInFees(ReservationEntity reservation) {
        List<ReservationRoomEntity> reservationRoomEntities = reservationRoomRepository
                .findByReservationEntity_IdAndIsActiveTrue(reservation.getId());

        for (ReservationRoomEntity reservationRoomEntity : reservationRoomEntities) {
            BigDecimal fee = calculateEarlyCheckInFee(reservationRoomEntity, reservation.getExpectedCheckIn());
            if (fee.signum() > 0) {
                folioService.applyEarlyCheckInFee(reservationRoomEntity, fee);
            }
        }
    }

    private BigDecimal calculateEarlyCheckInFee(ReservationRoomEntity reservationRoomEntity,
                                                Timestamp expectedCheckIn) {
        if (reservationRoomEntity.getActualCheckIn() == null || expectedCheckIn == null) {
            return BigDecimal.ZERO;
        }

        Instant actualCheckIn = reservationRoomEntity.getActualCheckIn();
        Instant expected = expectedCheckIn.toInstant();
        if (!actualCheckIn.isBefore(expected)) {
            return BigDecimal.ZERO;
        }

        BigDecimal rate = determineEarlyCheckInRate(actualCheckIn, expectedCheckIn);
        if (rate.signum() <= 0 || reservationRoomEntity.getPriceAtBooking() == null) {
            return BigDecimal.ZERO;
        }

        return reservationRoomEntity.getPriceAtBooking().multiply(rate).setScale(2, RoundingMode.HALF_UP);
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
            BigDecimal depositAmount) {
        List<ReservationRoomEntity> reservationRoomEntities = reservationRoomService.createRoomAllocations(
                reservation,
                request);

        for (ReservationRoomEntity reservationRoomEntity : reservationRoomEntities) {
            folioService.createFolioWithDepositItem(reservationRoomEntity, depositAmount);
        }
    }

    private ReservationEntity saveReservation(ReservationRequest request, CustomerEntity customer,
                                              BigDecimal depositAmount) {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setCode(RandomUtils.generateReservationCode("RS"));
        reservation.setStatus(ReservationStatus.PENDING_DEPOSIT);
        reservation.setCreatedAt(Timestamp.from(Instant.now()));
        reservation.setIsActive(true);
        updateReservationFields(reservation, request, customer, depositAmount);
        return reservationRepository.save(reservation);
    }

    private void updateReservationFields(
            ReservationEntity reservation,
            ReservationRequest request,
            CustomerEntity customer,
            BigDecimal depositAmount) {
        reservation.setCustomerEntity(customer);
        reservation.setExpectedCheckIn(request.checkInDate());
        reservation.setExpectedCheckOut(request.checkOutDate());
        reservation.setTotalDeposit(depositAmount);
        reservation.setNumberOfMembers(request.numberOfMembers() != null ? request.numberOfMembers() : 1);
        reservation.setNote(request.note());
    }
}
