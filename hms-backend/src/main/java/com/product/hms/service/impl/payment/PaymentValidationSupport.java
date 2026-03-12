package com.product.hms.service.impl.payment;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.PaymentTransactionEntity;
import com.product.hms.enums.FolioItemStatus;
import com.product.hms.enums.PaymentMethod;
import com.product.hms.enums.PaymentTransactionStatus;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.repository.FolioItemRepository;
import com.product.hms.repository.PaymentTransactionRepository;

import java.math.BigDecimal;
import java.util.List;

public final class PaymentValidationSupport {
    /**
     * Kiểm tra xem danh sách folio item IDs có null hoặc rỗng hay không.
     * Nếu có, ném ra một BadRequestException với mã lỗi và thông báo phù hợp.
     *
     * @param folioItemIds Danh sách folio item IDs cần kiểm tra.
     */
    public static void validateFolioItemIdsNullOrEmpty(List<Long> folioItemIds) {
        if (folioItemIds == null || folioItemIds.isEmpty()) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Folio item IDs must not be empty");
        }
    }

    /**
     * Kiểm tra xem số tiền đặt cọc có hợp lệ hay không (phải lớn hơn hoặc bằng 0).
     *
     * @param depositAmount Số tiền đặt cọc cần kiểm tra.
     * @return Số tiền đặt cọc đã được xác thực (nếu hợp lệ).
     */
    public static BigDecimal validateDepositAmountNegative(BigDecimal depositAmount) {
        depositAmount = depositAmount == null ? BigDecimal.ZERO : depositAmount;
        if (depositAmount.signum() < 0) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Deposit amount must be >= 0");
        }
        return depositAmount;
    }

    /**
     * Kiểm tra xem tất cả các folio item IDs trong danh sách có tồn tại trong cơ sở dữ liệu hay không.
     * Nếu có bất kỳ folio item nào không tồn tại, ném ra một BadRequestException với mã lỗi và thông báo phù hợp.
     *
     * @param folioItemIds        Danh sách folio item IDs cần kiểm tra.
     * @param folioItemRepository Repository để truy vấn cơ sở dữ liệu về folio items.
     * @return Danh sách các FolioItemEntity tương ứng với các folio item IDs đã được xác thực (nếu tất cả đều tồn tại).
     */
    public static List<FolioItemEntity> validateFolioItemsExistence(List<Long> folioItemIds, FolioItemRepository folioItemRepository) {
        List<FolioItemEntity> selectedFolioItemEntities = folioItemRepository.findAllById(folioItemIds);
        if (selectedFolioItemEntities.size() != folioItemIds.size()) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Some folioEntity items not found");
        }

        return selectedFolioItemEntities;
    }

    /**
     * Kiểm tra xem tất cả các folio item trong danh sách có thuộc về folio hay không.
     *
     * @param folioEntity       FolioEntity mà các folio item cần được kiểm tra thuộc về.
     * @param folioItemEntities Danh sách các FolioItemEntity cần kiểm tra.
     */
    public static void validateFolioItemsBelongToFolio(FolioEntity folioEntity, List<FolioItemEntity> folioItemEntities) {
        for (FolioItemEntity folioItemEntity : folioItemEntities) {
            if (!folioItemEntity.getFolioEntity().getId().equals(folioEntity.getId())) {
                throw new BadRequestException(
                        ErrorCode.INVALID_REQUEST,
                        "Folio folioItemEntity " + folioItemEntity.getId() + " does not belong to this folioEntity"
                );
            }
        }
    }

    /**
     * Kiểm tra xem tất cả các folio item trong danh sách có đang ở trạng thái UNPAID hay không.
     *
     * @param folioItemEntities Danh sách các FolioItemEntity cần kiểm tra.
     */
    public static void validateFolioItemNotPaid(List<FolioItemEntity> folioItemEntities) {
        for (FolioItemEntity folioItemEntity : folioItemEntities) {
            if (folioItemEntity.getStatus() != FolioItemStatus.UNPAID) {
                throw new BadRequestException(
                        ErrorCode.INVALID_REQUEST,
                        "Folio folioItemEntity " + folioItemEntity.getId() + " is not in UNPAID status"
                );
            }
        }
    }

    /**
     * Kiểm tra xem số tiền đặt cọc có vượt quá tổng giá của các folio item đã chọn hay không.
     *
     * @param folioItemEntities Danh sách các FolioItemEntity đã được xác thực và cần kiểm tra tổng giá.
     * @param depositAmount     Số tiền đặt cọc cần kiểm tra.
     * @return Tổng giá của các folio item đã chọn (nếu số tiền đặt cọc hợp lệ).
     */
    public static BigDecimal validateDepositAmountNotExceedTotal(List<FolioItemEntity> folioItemEntities, BigDecimal depositAmount) {
        BigDecimal selectedItemsTotalPrice = folioItemEntities.stream()
                .map(FolioItemEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (depositAmount.compareTo(selectedItemsTotalPrice) > 0) {
            throw new BadRequestException(
                    ErrorCode.INVALID_REQUEST,
                    "Deposit amount cannot exceed selected folioEntity items total"
            );
        }

        return selectedItemsTotalPrice;
    }

    /**
     * Kiểm tra xem phương thức thanh toán có hợp lệ hay không (phải là một giá trị hợp lệ của enum PaymentMethod).
     *
     * @param paymentMethodStr Chuỗi đại diện cho phương thức thanh toán cần kiểm tra.
     * @return Giá trị enum PaymentMethod tương ứng nếu chuỗi hợp lệ.
     */
    public static PaymentMethod validatePaymentMethodExistence(String paymentMethodStr) {
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(paymentMethodStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Invalid payment method: " + paymentMethodStr);
        }
        return paymentMethod;
    }

    /**
     * Kiểm tra sự tồn tại của một Payment.
     *
     * @param paymentTransactionId         ID của payment cần kiểm tra.
     * @param paymentTransactionRepository Repository để truy vấn cơ sở dữ liệu về payment transactions.
     * @return Giá trị của PaymentTransactionEntity nếu tồn tại.
     */
    public static PaymentTransactionEntity validatePaymentTransactionExistence(Long paymentTransactionId, PaymentTransactionRepository paymentTransactionRepository) {
        return paymentTransactionRepository.findById(paymentTransactionId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.PAYMENT_NOT_FOUND, "Payment transaction not found with ID: " + paymentTransactionId));
    }

    /**
     * Kiểm tra xem một PaymentTransactionEntity có đang ở trạng thái SUCCESS hay không, nếu có thì ném ra một BadRequestException với mã lỗi và thông báo phù hợp.
     *
     * @param paymentTransactionEntity PaymentTransactionEntity cần kiểm tra trạng thái.
     */
    public static void validatePaymentTransactionNotCompleted(PaymentTransactionEntity paymentTransactionEntity) {
        if (paymentTransactionEntity.getStatus().equals(PaymentTransactionStatus.SUCCESS.getDbValue())) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Payment transaction with ID: " + paymentTransactionEntity.getId() + " is already completed");
        }
    }

    /**
     * Kiểm tra xem phương thức thanh toán của một PaymentTransactionEntity có khớp với phương thức thanh toán mong đợi hay không, nếu không thì ném ra một BadRequestException với mã lỗi và thông báo phù hợp.
     *
     * @param paymentTransactionEntity PaymentTransactionEntity cần kiểm tra phương thức thanh toán.
     * @param expectedMethod           Phương thức thanh toán mong đợi để so sánh với phương thức thanh toán của PaymentTransactionEntity.
     */
    public static void validatePaymentTransactionMethod(PaymentTransactionEntity paymentTransactionEntity, PaymentMethod expectedMethod) {
        if (!paymentTransactionEntity.getPaymentMethod().equals(expectedMethod.getDbValue())) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST, "Payment transaction with ID: " + paymentTransactionEntity.getId() + " does not have expected payment method: " + expectedMethod);
        }
    }
}
