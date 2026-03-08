package com.product.hms.service.impl;

import com.product.hms.constants.Description;
import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.FolioItemEntity;
import com.product.hms.enums.FolioItemStatus;
import com.product.hms.enums.FolioItemType;
import com.product.hms.repository.FolioItemRepository;
import com.product.hms.service.FolioItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
}
