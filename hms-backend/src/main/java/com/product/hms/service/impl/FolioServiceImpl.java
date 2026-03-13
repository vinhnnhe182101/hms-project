package com.product.hms.service.impl;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomEntity;
import com.product.hms.entity.ServiceBookingEntity;
import com.product.hms.enums.FolioStatus;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.repository.FolioRepository;
import com.product.hms.service.FolioItemService;
import com.product.hms.service.FolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.product.hms.constants.Reservation;

@Service
@RequiredArgsConstructor
@Transactional
public class FolioServiceImpl implements FolioService {
    private final FolioRepository folioRepository;
    private final FolioItemService folioItemService;

    /**
     * Tạo folio mới khi có đặt cọc phòng, tạo mới vì lúc này chưa có bất kỳ charge nào phát sinh, chỉ có deposit.
     *
     * @param reservationRoomEntity reservation room allocation liên quan đến folio này.
     * @param depositAmount         số tiền đặt cọc (deposit) ban đầu, sẽ được tính vào total charges của folio.
     * @return FolioEntity đã được lưu vào database với thông tin ban đầu.
     */
    private FolioEntity createFolio(ReservationRoomEntity reservationRoomEntity, BigDecimal depositAmount) {
        FolioEntity folio = new FolioEntity();
        folio.setReservationRoomEntity(reservationRoomEntity);
        folio.setTotalCharges(depositAmount);
        folio.setTotalPaid(BigDecimal.ZERO);
        folio.setBalance(depositAmount);
        folio.setStatus(FolioStatus.OPEN);
        folio.setIsActive(true);
        return folioRepository.save(folio);
    }

    @Override
    public void createFolioWithDepositItem(ReservationRoomEntity reservationRoomEntity, BigDecimal depositAmount) {
        FolioEntity savedFolio = createFolio(reservationRoomEntity, depositAmount);
        folioItemService.createFolioItemForDeposit(savedFolio, depositAmount);
    }

    @Override
    public FolioEntity createFolioForBooking(ReservationRoomEntity reservationRoomEntity, BigDecimal totalAmount) {
        FolioEntity folio = new FolioEntity();
        folio.setReservationRoomEntity(reservationRoomEntity);
        folio.setTotalCharges(totalAmount);

        // totalPaid will be 20% of totalAmount (deposit percentage)
        BigDecimal totalPaid = totalAmount.multiply(Reservation.DEPOSIT_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);

        folio.setTotalPaid(totalPaid);
        folio.setBalance(totalAmount.subtract(totalPaid));
        folio.setStatus(FolioStatus.OPEN);
        folio.setIsActive(true);
        FolioEntity savedFolio = folioRepository.save(folio);

        folioItemService.createRoomChargeItem(savedFolio, totalAmount);
        
        return savedFolio;
    }

    @Override
    public void createRefundItem(ReservationRoomEntity reservationRoomEntity, BigDecimal refundAmount) {
        // STEP 1: Tìm folioEntity dựa vào reservation room
        FolioEntity folioEntity = folioRepository.findByReservationRoomEntity(reservationRoomEntity)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservationRoomEntity ID: " + reservationRoomEntity.getId()
                ));

        // STEP 2: Tạo folioEntity item cho refund (số tiền âm)
        folioItemService.createRefundItem(folioEntity, refundAmount);

        // STEP 3: Cập nhật tổng charges và balance của folioEntity
        folioEntity.setTotalCharges(folioEntity.getTotalCharges().subtract(refundAmount));
        folioEntity.setBalance(folioEntity.getBalance().subtract(refundAmount));
        folioRepository.save(folioEntity);
    }

    @Override
    public void createCancellationFeeItem(ReservationRoomEntity reservationRoomEntity, BigDecimal cancellationAmount) {
        // STEP 1: Tìm folio dựa vào reservation room
        FolioEntity folio = folioRepository.findByReservationRoomEntity(reservationRoomEntity)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservationRoomEntity ID: " + reservationRoomEntity.getId()
                ));

        // STEP 2: Tạo folio item cho cancellation fee (số tiền dương)
        folioItemService.createCancellationFeeItem(folio, cancellationAmount);

        // STEP 3: Cập nhật lại folio totals
        recalculateFolioTotals(folio);
    }

    @Override
    public void addServiceCharge(ServiceBookingEntity serviceBookingEntity) {
        // STEP 1: Tìm folioEntity dựa vào reservation room của service booking
        FolioEntity folioEntity = folioRepository.findByReservationRoomEntity(serviceBookingEntity.getReservationRoomEntity())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservation room ID: " + serviceBookingEntity.getReservationRoomEntity().getId()
                ));

        // STEP 2: Tính chargeAmount từ quantity và priceAtBooking
        BigDecimal chargeAmount = serviceBookingEntity.getPriceAtBooking().multiply(BigDecimal.valueOf(serviceBookingEntity.getQuantity()));

        // STEP 3: Tạo mới folio item cho service charge
        folioItemService.createServiceChargeItem(folioEntity, serviceBookingEntity, chargeAmount);

        // STEP 4: Tính lại tổng charges và balance của folioEntity
        recalculateFolioTotals(folioEntity);
    }

    @Override
    public void updateServiceCharge(ServiceBookingEntity serviceBookingEntity) {
        // STEP 1: Tìm folioEntity dựa vào reservation room của service booking
        FolioEntity folioEntity = folioRepository.findByReservationRoomEntity(serviceBookingEntity.getReservationRoomEntity())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservation room ID: " + serviceBookingEntity.getReservationRoomEntity().getId()
                ));

        // STEP 2: Tính lại chargeAmount từ quantity và priceAtBooking
        BigDecimal chargeAmount = serviceBookingEntity.getPriceAtBooking().multiply(BigDecimal.valueOf(serviceBookingEntity.getQuantity()));

        // STEP 3: Chỉ cho phép cập nhật số lượng, không cho phép thêm mới hoặc huỷ dịch vụ cũ
        folioItemService.findActiveByServiceBooking(serviceBookingEntity)
                .ifPresentOrElse(
                        folioItemEntity -> folioItemService.updateServiceChargeItem(folioItemEntity, serviceBookingEntity.getQuantity(), chargeAmount),
                        () -> {
                            throw new BusinessException(
                                    ErrorCode.INVALID_REQUEST,
                                    "Service Booking ID: " + serviceBookingEntity.getId() + " not found in any active folio item for reservation room ID: " + serviceBookingEntity.getReservationRoomEntity().getId());
                        }
                );

        // STEP 4: Tính lại tổng charges và balance của folioEntity để tránh lỗi cộng dồn delta
        recalculateFolioTotals(folioEntity);
    }

    @Override
    public void cancelServiceCharge(ServiceBookingEntity serviceBookingEntity) {
        FolioEntity folioEntity = folioRepository.findByReservationRoomEntity(serviceBookingEntity.getReservationRoomEntity())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservation room ID: " + serviceBookingEntity.getReservationRoomEntity().getId()
                ));

        folioItemService.findActiveByServiceBooking(serviceBookingEntity).ifPresent(item -> {
            folioItemService.voidServiceChargeItem(item);
            recalculateFolioTotals(folioEntity);
        });
    }

    @Override
    public void applyEarlyCheckInFee(ReservationRoomEntity reservationRoomEntity, BigDecimal feeAmount) {
        if (feeAmount == null || feeAmount.signum() <= 0) {
            return;
        }

        FolioEntity folioEntity = folioRepository.findByReservationRoomEntity(reservationRoomEntity)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservationRoomEntity ID: " + reservationRoomEntity.getId()
                ));

        folioItemService.createEarlyCheckInFeeItem(folioEntity, feeAmount);
        recalculateFolioTotals(folioEntity);
    }

    @Override
    public void applyLateCheckOutFee(ReservationRoomEntity reservationRoomEntity, BigDecimal feeAmount) {
        if (feeAmount == null || feeAmount.signum() <= 0) {
            return;
        }

        FolioEntity folio = folioRepository.findByReservationRoomEntity(reservationRoomEntity)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservationRoomEntity ID: " + reservationRoomEntity.getId()
                ));

        folioItemService.createLateCheckOutFeeItem(folio, feeAmount);
        recalculateFolioTotals(folio);
    }

    /**
     * Tính lại tổng phí và số tiền còn nợ của folioEntity sau khi có sự thay đổi về folioEntity item.
     *
     * @param folioEntity folio entity cần được tính lại tổng phí và số tiền còn nợ.
     */
    private void recalculateFolioTotals(FolioEntity folioEntity) {
        BigDecimal totalCharges = folioItemService.calculateTotalCharges(folioEntity);
        if (totalCharges == null) {
            totalCharges = BigDecimal.ZERO;
        }

        BigDecimal totalPaid = folioEntity.getTotalPaid();
        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }

        folioEntity.setTotalCharges(totalCharges);
        folioEntity.setBalance(totalCharges.subtract(totalPaid));
        folioRepository.save(folioEntity);
    }

    @Override
    public void addPaidAmount(FolioEntity folio, BigDecimal paidAmount) {
        if (folio == null || paidAmount == null) return;
        BigDecimal currentTotalPaid = folio.getTotalPaid() != null ? folio.getTotalPaid() : BigDecimal.ZERO;
        folio.setTotalPaid(currentTotalPaid.add(paidAmount));
        BigDecimal totalCharges = folio.getTotalCharges() != null ? folio.getTotalCharges() : BigDecimal.ZERO;
        folio.setBalance(totalCharges.subtract(folio.getTotalPaid()));
        folioRepository.save(folio);
    }

    @Override
    public void handleRoomChangeAdjustment(
            ReservationRoomEntity reservationRoom,
            RoomEntity newRoom,
            BigDecimal changeFee
    ) {
        // Lưu lại thông tin phòng cũ
        String oldRoomNumber = reservationRoom.getRoomEntity() != null ? reservationRoom.getRoomEntity().getRoomNumber() : null;
        String oldRoomClass = reservationRoom.getRoomClassEntity() != null ? reservationRoom.getRoomClassEntity().getName() : null;
        String newRoomNumber = newRoom.getRoomNumber();
        String newRoomClass = newRoom.getRoomClassEntity().getName();
        // Lấy FolioEntity liên quan
        FolioEntity folio = folioRepository.findByReservationRoomEntity(reservationRoom)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservation room ID: " + reservationRoom.getId()
                ));
        folioItemService.createFolioItemForRoomChangeAdjustment(
                folio,
                changeFee,
                oldRoomNumber,
                oldRoomClass,
                newRoomNumber,
                newRoomClass
        );
    }
}
