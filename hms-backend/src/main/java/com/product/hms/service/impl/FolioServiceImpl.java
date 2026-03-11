package com.product.hms.service.impl;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.ReservationRoomEntity;
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
     * @param reservationRoomEntity
     * @param depositAmount
     * @return
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
        // STEP 1: Tìm folio dựa vào reservation room
        FolioEntity folio = folioRepository.findByReservationRoomEntity(reservationRoomEntity)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservationRoomEntity ID: " + reservationRoomEntity.getId()
                ));

        // STEP 2: Tạo folio item cho refund (số tiền âm)
        folioItemService.createRefundItem(folio, refundAmount);

        // STEP 3: Cập nhật tổng charges và balance của folio
        folio.setTotalCharges(folio.getTotalCharges().subtract(refundAmount));
        folio.setBalance(folio.getBalance().subtract(refundAmount));
        folioRepository.save(folio);
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
    public void updateServiceCharge(ServiceBookingEntity serviceBooking, BigDecimal chargeAmount) {
        // STEP 1: Tìm folio dựa vào reservation room của service booking
        FolioEntity folio = folioRepository.findByReservationRoomEntity(serviceBooking.getReservationRoomEntity())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservation room ID: " + serviceBooking.getReservationRoomEntity().getId()
                ));

        // STEP 2: Tìm folio item đang active cho service booking này
        // TODO: Nên chuyển về chỉ update total price của folio item
        folioItemService.findActiveByServiceBooking(serviceBooking)
                .ifPresentOrElse(
                        item -> folioItemService.updateServiceChargeItem(item, serviceBooking.getQuantity(), chargeAmount),
                        () -> folioItemService.createServiceChargeItem(folio, serviceBooking, chargeAmount)
                );

        // STEP 3: Tính lại tổng charges và balance của folio để tránh lỗi cộng dồn delta
        recalculateFolioTotals(folio);
    }

    @Override
    public void cancelServiceCharge(ServiceBookingEntity serviceBooking) {
        FolioEntity folio = folioRepository.findByReservationRoomEntity(serviceBooking.getReservationRoomEntity())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservation room ID: " + serviceBooking.getReservationRoomEntity().getId()
                ));

        folioItemService.findActiveByServiceBooking(serviceBooking).ifPresent(item -> {
            folioItemService.voidServiceChargeItem(item);
            recalculateFolioTotals(folio);
        });
    }

    @Override
    public void applyEarlyCheckInFee(ReservationRoomEntity reservationRoomEntity, BigDecimal feeAmount) {
        if (feeAmount == null || feeAmount.signum() <= 0) {
            return;
        }

        FolioEntity folio = folioRepository.findByReservationRoomEntity(reservationRoomEntity)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for reservationRoomEntity ID: " + reservationRoomEntity.getId()
                ));

        folioItemService.createEarlyCheckInFeeItem(folio, feeAmount);
        recalculateFolioTotals(folio);
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
     * Tính lại tổng phí và số tiền còn nợ của folioEntity sau khi có sự thay đổi về folioEntity item
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
}
