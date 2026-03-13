package com.product.hms.service;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.FolioItemEntity;
import com.product.hms.entity.ServiceBookingEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FolioItemService {
    void createFolioItemForDeposit(FolioEntity folio, BigDecimal depositAmount);
    void createRoomChargeItem(FolioEntity folio, BigDecimal amount);

    /**
     * Tạo một folio item cho khoản hoàn tiền (refund)
     *
     * @param folio        folio entity
     * @param refundAmount số tiền hoàn trả (positive value, sẽ được lưu dưới dạng âm trong folio item)
     */
    void createRefundItem(FolioEntity folio, BigDecimal refundAmount);

    /**
     * Tạo một folio item cho khoản phí hủy phòng (cancellation fee) khi khách hàng hủy đặt phòng và không được hoàn tiền.
     *
     * @param folio              folio entity
     * @param cancellationAmount số tiền phí hủy phòng (positive value)
     */
    void createCancellationFeeItem(FolioEntity folio, BigDecimal cancellationAmount);

    /**
     * Tạo một folio item cho khoản phí check-in sớm (early check-in fee) khi khách hàng đến nhận phòng trước giờ quy định.
     *
     * @param folio     folio entity
     * @param feeAmount số tiền phí check-in sớm (positive value)
     */
    void createEarlyCheckInFeeItem(FolioEntity folio, BigDecimal feeAmount);

    /**
     * Tạo một folio item cho khoản phí check-out muộn (late check-out fee) khi khách hàng trả phòng sau giờ quy định.
     *
     * @param folio     folio entity
     * @param feeAmount số tiền phí check-out muộn (positive value)
     */
    void createLateCheckOutFeeItem(FolioEntity folio, BigDecimal feeAmount);

    /**
     * Tạo một folio item cho khoản phí dịch vụ (service charge) khi khách hàng sử dụng dịch vụ trong thời gian lưu trú.
     *
     * @param folio          folio entity
     * @param serviceBooking đặt dịch vụ liên quan đến khoản phí này
     * @param chargeAmount   số tiền phí dịch vụ (positive value)
     */
    void createServiceChargeItem(FolioEntity folio, ServiceBookingEntity serviceBooking, BigDecimal chargeAmount);

    /**
     * Tìm kiếm một folio item đang hoạt động (active) liên quan đến một service booking cụ thể. Điều này hữu ích để cập nhật hoặc hủy bỏ khoản phí dịch vụ nếu cần.
     *
     * @param serviceBooking đặt dịch vụ mà bạn muốn tìm kiếm folio item liên quan đến nó
     * @return Một Optional chứa folio item nếu tìm thấy và đang hoạt động, hoặc Optional.empty() nếu không tìm thấy hoặc không còn hoạt động
     */
    Optional<FolioItemEntity> findActiveByServiceBooking(ServiceBookingEntity serviceBooking);

    /**
     * Cập nhật một folio item đã tồn tại cho khoản phí dịch vụ. Thường được sử dụng khi có sự thay đổi về số lượng dịch vụ hoặc tổng giá trị cần tính lại.
     *
     * @param folioItem  folio item đã tồn tại cần được cập nhật
     * @param quantity   số lượng dịch vụ đã sử dụng (có thể thay đổi so với lần tính trước đó)
     * @param totalPrice tổng giá trị mới của khoản phí dịch vụ sau khi cập nhật (có thể thay đổi so với lần tính trước đó). Đây là giá trị cuối cùng sẽ được lưu vào folio item sau khi cập nhật.
     */
    void updateServiceChargeItem(FolioItemEntity folioItem, Integer quantity, BigDecimal totalPrice);

    /**
     * Hủy bỏ một folio item liên quan đến khoản phí dịch vụ. Thay vì xóa bỏ hoàn toàn, phương pháp này sẽ đánh dấu folio item là không còn hoạt động (isActive = false).
     *
     * @param folioItem folio item liên quan đến khoản phí dịch vụ cần được hủy bỏ.
     */
    void voidServiceChargeItem(FolioItemEntity folioItem);

    /**
     * Tính toán tổng số tiền các khoản phí (charges) hiện đang hoạt động trong một folio.
     *
     * @param folio folio entity mà bạn muốn tính tổng charges cho nó
     * @return Tổng số tiền các khoản phí (charges) đang hoạt động trong folio.
     */
    BigDecimal calculateTotalCharges(FolioEntity folio);

    /**
     * Đánh dấu danh sách folio item là đã thanh toán (PAID)
     *
     * @param items danh sách folio item cần cập nhật trạng thái
     */
    void markItemsAsPaid(List<FolioItemEntity> items);

    /**
     * Tạo FolioItem điều chỉnh khi khách chuyển phòng.
     *
     * @param folio            Folio liên quan
     * @param amount           Số tiền điều chỉnh (có thể âm hoặc dương)
     * @param oldRoomNumber    Số phòng cũ
     * @param oldRoomClassName Hạng phòng cũ
     * @param newRoomNumber    Số phòng mới
     * @param newRoomClassName Hạng phòng mới
     */
    void createFolioItemForRoomChangeAdjustment(
            FolioEntity folio,
            java.math.BigDecimal amount,
            String oldRoomNumber,
            String oldRoomClassName,
            String newRoomNumber,
            String newRoomClassName
    );
}
