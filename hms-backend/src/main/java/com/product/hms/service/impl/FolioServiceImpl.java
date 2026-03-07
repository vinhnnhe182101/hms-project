package com.product.hms.service.impl;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.enums.FolioStatus;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.repository.FolioRepository;
import com.product.hms.service.FolioItemService;
import com.product.hms.service.FolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FolioServiceImpl implements FolioService {
    private final FolioRepository folioRepository;
    private final FolioItemService folioItemService;

    private FolioEntity createFolio(ReservationRoomEntity allocation, BigDecimal depositAmount) {
        FolioEntity folio = new FolioEntity();
        folio.setReservationRoomAllocation(allocation);
        folio.setTotalCharges(depositAmount);
        folio.setTotalPaid(BigDecimal.ZERO);
        folio.setBalance(depositAmount);
        folio.setStatus(FolioStatus.OPEN);
        folio.setIsActive(true);
        return folioRepository.save(folio);
    }

    @Override
    public void createFolioWithDepositItem(ReservationRoomEntity allocation, BigDecimal depositAmount) {
        FolioEntity savedFolio = createFolio(allocation, depositAmount);
        folioItemService.createFolioItemForDeposit(savedFolio, depositAmount);
    }

    @Override
    public void createRefundItem(ReservationRoomEntity allocation, BigDecimal refundAmount) {
        FolioEntity folio = folioRepository.findByReservationRoomAllocation(allocation)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for allocation ID: " + allocation.getId()
                ));

        // Create refund item (negative amount)
        folioItemService.createRefundItem(folio, refundAmount);

        // Update folio balance (reduce by refund amount)
        folio.setTotalCharges(folio.getTotalCharges().subtract(refundAmount));
        folio.setBalance(folio.getBalance().subtract(refundAmount));
        folioRepository.save(folio);
    }

    @Override
    public void createCancellationFeeItem(ReservationRoomEntity allocation, BigDecimal cancellationAmount) {
        FolioEntity folio = folioRepository.findByReservationRoomAllocation(allocation)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Folio not found for allocation ID: " + allocation.getId()
                ));

        // Create cancellation fee item (no balance change, just record the fee)
        folioItemService.createCancellationFeeItem(folio, cancellationAmount);
        // Balance remains the same (customer forfeits deposit)
    }
}
