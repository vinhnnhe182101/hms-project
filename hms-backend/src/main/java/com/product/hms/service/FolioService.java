package com.product.hms.service;

import com.product.hms.entity.FolioEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.ServiceBookingEntity;

import java.math.BigDecimal;

public interface FolioService {
    void createFolioWithDepositItem(ReservationRoomEntity allocation, BigDecimal depositAmount);
    FolioEntity createFolioForBooking(ReservationRoomEntity allocation, BigDecimal totalAmount);

    /**
     * Tạo một folio item cho khoản hoàn tiền (refund) khi khách hàng hủy đặt phòng.
     *
     * @param allocation   reservation room allocation liên quan đến khoản hoàn tiền này
     * @param refundAmount số tiền hoàn trả (positive value, sẽ được lưu dưới dạng âm trong folio item)
     */
    void createRefundItem(ReservationRoomEntity allocation, BigDecimal refundAmount);

    /**
     * Tạo một folio item cho khoản phí hủy phòng (cancellation fee) khi khách hàng hủy đặt phòng và không được hoàn tiền.
     *
     * @param allocation         reservation room allocation liên quan đến khoản phí hủy phòng này
     * @param cancellationAmount số tiền phí hủy phòng (positive value)
     */
    void createCancellationFeeItem(ReservationRoomEntity allocation, BigDecimal cancellationAmount);

    /**
     * Áp dụng khoản phí check-in sớm (early check-in fee) khi khách hàng đến nhận phòng trước giờ quy định.
     *
     * @param allocation reservation room allocation liên quan đến khoản phí check-in sớm này
     * @param feeAmount  số tiền phí check-in sớm (positive value)
     */
    void applyEarlyCheckInFee(ReservationRoomEntity allocation, BigDecimal feeAmount);

    /**
     * Áp dụng khoản phí check-out muộn (late check-out fee) khi khách hàng trả phòng sau giờ quy định.
     *
     * @param allocation reservation room allocation liên quan đến khoản phí check-out muộn này
     * @param feeAmount  số tiền phí check-out muộn (positive value)
     */
    void applyLateCheckOutFee(ReservationRoomEntity allocation, BigDecimal feeAmount);

    /**
     * Cập nhật khoản phí dịch vụ (service charge) khi có thay đổi về số lượng.
     *
     * @param serviceBooking đặt dịch vụ liên quan đến khoản phí này
     * @param chargeAmount   số tiền phí dịch vụ mới (positive value)
     */
    // TODO: Nên bỏ chargeAmount ra khỏi tham số, thay vào đó sẽ tính toán dựa trên serviceBooking
    void updateServiceCharge(ServiceBookingEntity serviceBooking, BigDecimal chargeAmount);

    /**
     * Hủy bỏ khoản phí dịch vụ (service charge) khi khách hàng hủy dịch vụ.
     *
     * @param serviceBooking đặt dịch vụ liên quan đến khoản phí này
     */
    void cancelServiceCharge(ServiceBookingEntity serviceBooking);
}
