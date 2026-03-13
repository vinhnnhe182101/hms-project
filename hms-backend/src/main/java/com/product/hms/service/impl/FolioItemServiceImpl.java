package com.product.hms.service.impl;

import com.product.hms.constants.Description;
import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.ServiceBookingEntity;
import com.product.hms.enums.FolioItemStatus;
import com.product.hms.enums.FolioItemType;
import com.product.hms.repository.FolioItemRepository;
import com.product.hms.service.FolioItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FolioItemServiceImpl implements FolioItemService {
    private final FolioItemRepository folioItemRepository;

    @Override
    public void createFolioItemForDeposit(FolioEntity folioEntity, BigDecimal depositAmount) {
        FolioItemEntity folioItemEntity = new FolioItemEntity();
        folioItemEntity.setFolioEntity(folioEntity);
        folioItemEntity.setType(FolioItemType.ROOM_CHARGE);
        folioItemEntity.setDescription(Description.DEPOSIT_FOR_ROOM_RESERVATION);
        folioItemEntity.setQuantity(1);
        folioItemEntity.setTotalPrice(depositAmount);
        folioItemEntity.setStatus(FolioItemStatus.UNPAID);
        folioItemEntity.setIsActive(true);
        folioItemRepository.save(folioItemEntity);
    }

    @Override
    public void createRefundItem(FolioEntity folioEntity, BigDecimal refundAmount) {
        FolioItemEntity folioItemEntity = new FolioItemEntity();
        folioItemEntity.setFolioEntity(folioEntity);
        folioItemEntity.setType(FolioItemType.ADJUSTMENT);
        folioItemEntity.setDescription(Description.REFUND_DEPOSIT_CANCELLATION);
        folioItemEntity.setQuantity(1);
        folioItemEntity.setTotalPrice(refundAmount.negate());  // NOTE: Negative amount = refund
        folioItemEntity.setStatus(FolioItemStatus.PAID);
        folioItemEntity.setIsActive(true);
        folioItemRepository.save(folioItemEntity);
    }

    @Override
    public void createCancellationFeeItem(FolioEntity folioEntity, BigDecimal cancellationAmount) {
        FolioItemEntity folioItemEntity = new FolioItemEntity();
        folioItemEntity.setFolioEntity(folioEntity);
        folioItemEntity.setType(FolioItemType.ADJUSTMENT);
        folioItemEntity.setDescription(Description.CANCELLATION_FEE_NO_REFUND);
        folioItemEntity.setQuantity(1);
        folioItemEntity.setTotalPrice(cancellationAmount);  // NOTE: Positive = fee charged
        folioItemEntity.setStatus(FolioItemStatus.PAID);
        folioItemEntity.setIsActive(true);
        folioItemRepository.save(folioItemEntity);
    }

    @Override
    public void createServiceChargeItem(FolioEntity folioEntity, ServiceBookingEntity serviceBooking, BigDecimal chargeAmount) {
        FolioItemEntity folioItemEntity = new FolioItemEntity();
        folioItemEntity.setFolioEntity(folioEntity);
        folioItemEntity.setType(FolioItemType.SERVICE_CHARGE);
        folioItemEntity.setServiceBookingEntity(serviceBooking);
        folioItemEntity.setDescription(Description.SERVICE_BOOKING_CHARGE);
        folioItemEntity.setQuantity(serviceBooking.getQuantity());
        folioItemEntity.setTotalPrice(chargeAmount);
        folioItemEntity.setStatus(FolioItemStatus.UNPAID);
        folioItemEntity.setIsActive(true);
        folioItemRepository.save(folioItemEntity);
    }

    @Override
    public Optional<FolioItemEntity> findActiveByServiceBooking(ServiceBookingEntity serviceBookingEntity) {
        return folioItemRepository.findByServiceBookingEntityAndIsActiveTrue(serviceBookingEntity);
    }

    @Override
    public void updateServiceChargeItem(FolioItemEntity folioItemEntity, Integer quantity, BigDecimal totalPrice) {
        folioItemEntity.setQuantity(quantity);
        folioItemEntity.setTotalPrice(totalPrice);
        folioItemRepository.save(folioItemEntity);
    }

    @Override
    public void voidServiceChargeItem(FolioItemEntity folioItemEntity) {
        folioItemEntity.setStatus(FolioItemStatus.VOID);
        folioItemEntity.setIsActive(false);
        folioItemEntity.setDescription(Description.SERVICE_BOOKING_CANCELED);
        folioItemRepository.save(folioItemEntity);
    }

    @Override
    public BigDecimal calculateTotalCharges(FolioEntity folioEntity) {
        List<FolioItemEntity> activeFolioItemEntities = folioItemRepository.findByFolioEntity_IdAndIsActiveTrue(folioEntity.getId());
        return activeFolioItemEntities.stream()
                .map(FolioItemEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void createEarlyCheckInFeeItem(FolioEntity folioEntity, BigDecimal feeAmount) {
        FolioItemEntity folioItemEntity = new FolioItemEntity();
        folioItemEntity.setFolioEntity(folioEntity);
        folioItemEntity.setType(FolioItemType.EARLY_CHECKIN_FEE);
        folioItemEntity.setDescription(Description.EARLY_CHECKIN_SURCHARGE);
        folioItemEntity.setQuantity(1);
        folioItemEntity.setTotalPrice(feeAmount);
        folioItemEntity.setStatus(FolioItemStatus.UNPAID);
        folioItemEntity.setIsActive(true);
        folioItemRepository.save(folioItemEntity);
    }

    @Override
    public void createLateCheckOutFeeItem(FolioEntity folioEntity, BigDecimal feeAmount) {
        FolioItemEntity folioItemEntity = new FolioItemEntity();
        folioItemEntity.setFolioEntity(folioEntity);
        folioItemEntity.setType(FolioItemType.LATE_CHECKOUT_FEE);
        folioItemEntity.setDescription(Description.LATE_CHECKOUT_SURCHARGE);
        folioItemEntity.setQuantity(1);
        folioItemEntity.setTotalPrice(feeAmount);
        folioItemEntity.setStatus(FolioItemStatus.UNPAID);
        folioItemEntity.setIsActive(true);
        folioItemRepository.save(folioItemEntity);
    }

    @Override
    public void markItemsAsPaid(List<FolioItemEntity> items) {
        if (items == null || items.isEmpty()) return;
        for (FolioItemEntity item : items) {
            item.setStatus(FolioItemStatus.PAID);
            folioItemRepository.save(item);
        }
    }

    @Override
    public void createFolioItemForRoomChangeAdjustment(
            FolioEntity folio,
            java.math.BigDecimal amount,
            String oldRoomNumber,
            String oldRoomClassName,
            String newRoomNumber,
            String newRoomClassName
    ) {
        FolioItemEntity folioItem = new FolioItemEntity();
        folioItem.setFolioEntity(folio);
        folioItem.setType(com.product.hms.enums.FolioItemType.ADJUSTMENT);
        folioItem.setTotalPrice(amount);
        folioItem.setStatus(com.product.hms.enums.FolioItemStatus.UNPAID);
        folioItem.setIsActive(true);
        folioItem.setDescription(String.format(
                "Room change from %s (%s) to %s (%s)",
                oldRoomNumber, oldRoomClassName, newRoomNumber, newRoomClassName
        ));
        folioItemRepository.save(folioItem);
    }
}
