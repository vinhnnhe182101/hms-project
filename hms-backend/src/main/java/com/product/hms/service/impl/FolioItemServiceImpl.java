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
    public void createFolioItemForDeposit(FolioEntity folio, BigDecimal depositAmount) {
        FolioItemEntity folioItem = new FolioItemEntity();
        folioItem.setFolioEntity(folio);
        folioItem.setType(FolioItemType.ROOM_CHARGE);
        folioItem.setDescription(Description.DEPOSIT_FOR_ROOM_RESERVATION);
        folioItem.setQuantity(1);
        folioItem.setTotalPrice(depositAmount);
        folioItem.setStatus(FolioItemStatus.UNPAID);
        folioItem.setIsActive(true);
        folioItemRepository.save(folioItem);
    }

    @Override
    public void createRoomChargeItem(FolioEntity folio, BigDecimal amount) {
        FolioItemEntity folioItem = new FolioItemEntity();
        folioItem.setFolioEntity(folio);
        folioItem.setType(FolioItemType.ROOM_CHARGE);
        folioItem.setDescription(Description.ROOM_CHARGE);
        folioItem.setQuantity(1);
        folioItem.setTotalPrice(amount);
        folioItem.setStatus(FolioItemStatus.UNPAID);
        folioItem.setIsActive(true);
        folioItemRepository.save(folioItem);
    }

    @Override
    public void createRefundItem(FolioEntity folio, BigDecimal refundAmount) {
        FolioItemEntity folioItem = new FolioItemEntity();
        folioItem.setFolioEntity(folio);
        folioItem.setType(FolioItemType.ADJUSTMENT);
        folioItem.setDescription(Description.REFUND_DEPOSIT_CANCELLATION);
        folioItem.setQuantity(1);
        folioItem.setTotalPrice(refundAmount.negate());  // Negative amount = refund
        folioItem.setStatus(FolioItemStatus.PAID);
        folioItem.setIsActive(true);
        folioItemRepository.save(folioItem);
    }

    @Override
    public void createCancellationFeeItem(FolioEntity folio, BigDecimal cancellationAmount) {
        FolioItemEntity folioItem = new FolioItemEntity();
        folioItem.setFolioEntity(folio);
        folioItem.setType(FolioItemType.ADJUSTMENT);
        folioItem.setDescription(Description.CANCELLATION_FEE_NO_REFUND);
        folioItem.setQuantity(1);
        folioItem.setTotalPrice(cancellationAmount);  // Positive = fee charged
        folioItem.setStatus(FolioItemStatus.PAID);
        folioItem.setIsActive(true);
        folioItemRepository.save(folioItem);
    }

    @Override
    public void createServiceChargeItem(FolioEntity folio, ServiceBookingEntity serviceBooking, BigDecimal chargeAmount) {
        FolioItemEntity folioItem = new FolioItemEntity();
        folioItem.setFolioEntity(folio);
        folioItem.setType(FolioItemType.SERVICE_CHARGE);
        folioItem.setServiceBookingEntity(serviceBooking);
        folioItem.setDescription(Description.SERVICE_BOOKING_CHARGE);
        folioItem.setQuantity(serviceBooking.getQuantity());
        folioItem.setTotalPrice(chargeAmount);
        folioItem.setStatus(FolioItemStatus.UNPAID);
        folioItem.setIsActive(true);
        folioItemRepository.save(folioItem);
    }

    @Override
    public Optional<FolioItemEntity> findActiveByServiceBooking(ServiceBookingEntity serviceBooking) {
        return folioItemRepository.findByServiceBookingEntityAndIsActiveTrue(serviceBooking);
    }

    @Override
    public void updateServiceChargeItem(FolioItemEntity folioItem, Integer quantity, BigDecimal totalPrice) {
        folioItem.setQuantity(quantity);
        folioItem.setTotalPrice(totalPrice);
        folioItemRepository.save(folioItem);
    }

    @Override
    public void voidServiceChargeItem(FolioItemEntity folioItem) {
        folioItem.setStatus(FolioItemStatus.VOID);
        folioItem.setIsActive(false);
        folioItem.setDescription(Description.SERVICE_BOOKING_CANCELED);
        folioItemRepository.save(folioItem);
    }

    @Override
    public BigDecimal calculateTotalCharges(FolioEntity folioEntity) {
        List<FolioItemEntity> activeItems = folioItemRepository.findByFolioEntity_IdAndIsActiveTrue(folioEntity.getId());
        return activeItems.stream()
                .map(item -> item.getTotalPrice() != null ? item.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void createEarlyCheckInFeeItem(FolioEntity folio, BigDecimal feeAmount) {
        FolioItemEntity folioItem = new FolioItemEntity();
        folioItem.setFolioEntity(folio);
        folioItem.setType(FolioItemType.EARLY_CHECKIN_FEE);
        folioItem.setDescription(Description.EARLY_CHECKIN_SURCHARGE);
        folioItem.setQuantity(1);
        folioItem.setTotalPrice(feeAmount);
        folioItem.setStatus(FolioItemStatus.UNPAID);
        folioItem.setIsActive(true);
        folioItemRepository.save(folioItem);
    }

    @Override
    public void createLateCheckOutFeeItem(FolioEntity folio, BigDecimal feeAmount) {
        FolioItemEntity folioItem = new FolioItemEntity();
        folioItem.setFolioEntity(folio);
        folioItem.setType(FolioItemType.LATE_CHECKOUT_FEE);
        folioItem.setDescription(Description.LATE_CHECKOUT_SURCHARGE);
        folioItem.setQuantity(1);
        folioItem.setTotalPrice(feeAmount);
        folioItem.setStatus(FolioItemStatus.UNPAID);
        folioItem.setIsActive(true);
        folioItemRepository.save(folioItem);
    }
}
