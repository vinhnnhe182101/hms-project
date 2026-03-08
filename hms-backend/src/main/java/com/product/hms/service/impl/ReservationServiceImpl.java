package com.product.hms.service.impl;

import com.product.hms.dto.request.BookingRequestDTO;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomAllocationEntity;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.repository.ReservationRepository;
import com.product.hms.repository.ReservationRoomAllocationRepository;
import com.product.hms.repository.RoomClassRepository;
import com.product.hms.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import com.product.hms.entity.RoomEntity;
import com.product.hms.repository.RoomRepository;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationRoomAllocationRepository allocationRepository;
    private final RoomClassRepository roomClassRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public Long createBooking(BookingRequestDTO request) {
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

        BigDecimal totalDeposit = BigDecimal.ZERO;
        if (request.getRooms() != null) {
            for (BookingRequestDTO.RoomBookingRequest r : request.getRooms()) {
                if (r.getTotal() != null) {
                    totalDeposit = totalDeposit.add(r.getTotal());
                }
            }
        }

        ReservationEntity reservation = new ReservationEntity();
        reservation.setCode("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.setCustomerEntity(customer);
        reservation.setExpectedCheckIn(Timestamp.from(request.getCheckIn()));
        reservation.setExpectedCheckOut(Timestamp.from(request.getCheckOut()));
        reservation.setStatus(ReservationStatus.PENDING_DEPOSIT);
        reservation.setTotalDeposit(totalDeposit);
        reservation.setNumberOfMembers(request.getGuests());
        reservation.setNote(request.getCustomer().getNote());
        reservation.setCreatedAt(Timestamp.from(Instant.now()));
        reservation.setIsActive(true);

        ReservationEntity savedReservation = reservationRepository.save(reservation);

        for (BookingRequestDTO.RoomBookingRequest roomReq : request.getRooms()) {
            RoomClassEntity roomClass = roomClassRepository.findById(roomReq.getId())
                    .orElseThrow(() -> new RuntimeException("Room Class not found: " + roomReq.getId()));

            List<RoomEntity> availableRooms = roomRepository.findAvailableRoomsByClass(
                    roomReq.getId(), 
                    Timestamp.from(request.getCheckIn()), 
                    Timestamp.from(request.getCheckOut()), 
                    List.of(ReservationStatus.PENDING_DEPOSIT, ReservationStatus.CONFIRMED, ReservationStatus.IN_HOUSE)
            );

            if (availableRooms.size() < roomReq.getQuantity()) {
                throw new RuntimeException("Không đủ phòng trống cho loại phòng: " + roomClass.getName());
            }

            Collections.shuffle(availableRooms);

            for (int i = 0; i < roomReq.getQuantity(); i++) {
                ReservationRoomAllocationEntity allocation = new ReservationRoomAllocationEntity();
                allocation.setReservationEntity(savedReservation);
                allocation.setRoomClassEntity(roomClass);
                allocation.setRoomEntity(availableRooms.get(i));
                allocation.setPriceAtBooking(roomReq.getPricePerNight() != null ? roomReq.getPricePerNight() : roomClass.getBasePrice());
                allocation.setNumberOfPeople(1);
                allocation.setIsActive(true);
                allocationRepository.save(allocation);
            }
        }

        return savedReservation.getId();
    }
}
