package com.product.hms.service.impl;

import com.product.hms.dto.request.ServiceBookingRequestDTO;
import com.product.hms.dto.response.ActiveAllocationResponseDTO;
import com.product.hms.entity.ReservationRoomAllocationEntity;
import com.product.hms.entity.ServiceBookingEntity;
import com.product.hms.entity.ServiceEntity;
import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.FolioItemEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.enums.ServiceBookingStatus;
import com.product.hms.repository.ReservationRoomAllocationRepository;
import com.product.hms.repository.ServiceBookingRepository;
import com.product.hms.repository.ServiceRepository;
import com.product.hms.repository.FolioRepository;
import com.product.hms.repository.FolioItemRepository;
import com.product.hms.service.ServiceBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceBookingServiceImpl implements ServiceBookingService {

    private final ReservationRoomAllocationRepository allocationRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final ServiceRepository serviceRepository;
    private final FolioRepository folioRepository;
    private final FolioItemRepository folioItemRepository;

    @Override
    public List<ActiveAllocationResponseDTO> getActiveAllocationsByCustomer(Long customerId) {

        List<ReservationRoomAllocationEntity> allocations = allocationRepository.findActiveAllocationsByCustomer(
                customerId, List.of(ReservationStatus.IN_HOUSE)
        );

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
            // Lấy Allocation để biết Booking nào và Phòng nào
            ReservationRoomAllocationEntity allocation = allocationRepository.findById(item.getAllocationId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin phòng sử dụng"));

            ServiceEntity service = serviceRepository.findById(item.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ: " + item.getServiceId()));

            ServiceBookingEntity serviceBookingEntity = new ServiceBookingEntity();
            serviceBookingEntity.setReservationEntity(allocation.getReservationEntity());
            serviceBookingEntity.setAllocationEntity(allocation);
            serviceBookingEntity.setServiceEntity(service);
            serviceBookingEntity.setQuantity(item.getQuantity());
            serviceBookingEntity.setPriceAtBooking(service.getPrice());
            serviceBookingEntity.setStatus(ServiceBookingStatus.PENDING);
            serviceBookingEntity.setIsActive(true);

            serviceBookingEntity = serviceBookingRepository.save(serviceBookingEntity);

            FolioEntity folioEntity = folioRepository.findByReservationId(allocation.getReservationEntity().getId())
                    .orElseGet(() -> { /*  tự động tạo một cái mới cho Booking này */
                        FolioEntity newFolio = new FolioEntity();
                        newFolio.setReservation(allocation.getReservationEntity());
                        newFolio.setTotalCharges(BigDecimal.ZERO);
                        newFolio.setTotalPaid(BigDecimal.ZERO);
                        newFolio.setBalance(BigDecimal.ZERO);
                        newFolio.setStatus("UNPAID");
                        newFolio.setIsActive(true);
                        return folioRepository.save(newFolio);
                    });

            BigDecimal itemTotalPrice = service.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            folioEntity.setTotalCharges(folioEntity.getTotalCharges().add(itemTotalPrice));
            folioEntity.setBalance(folioEntity.getBalance().add(itemTotalPrice));
            folioRepository.save(folioEntity);

            FolioItemEntity folioItem = new FolioItemEntity();
            folioItem.setFolioEntity(folioEntity);
            folioItem.setType("SERVICE_CHARGE");
            folioItem.setServiceBookingEntity(serviceBookingEntity);
            folioItem.setDescription("Sử dụng dịch vụ: " + service.getName() + " (Phòng: " + 
                  (allocation.getRoomEntity() != null ? allocation.getRoomEntity().getRoomNumber() : "Chưa xếp phòng") + ")");
            folioItem.setQuantity(item.getQuantity());
            folioItem.setTotalPrice(itemTotalPrice);
            folioItem.setStatus("UNPAID");
            folioItem.setIsActive(true);

            folioItemRepository.save(folioItem);
        }
    }
}
