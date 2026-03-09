package com.product.hms.service;

import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.response.PaymentResponse;
import com.product.hms.dto.response.ReservationRoomCheckOutResponse;
import com.product.hms.dto.response.ReservationRoomFolioResponse;

/**
 * Service interface for reservation room operations (check-out, folio management, payment)
 */
public interface ReservationRoomService {

    /**
     * Lấy thông tin chi tiết của ReservationRoom, bao gồm thông tin phòng, khách lưu trú, các mục trong folio và số dư hiện tại.
     *
     * @param reservationRoomId ID của reservation room mà bạn muốn lấy thông tin.
     * @return ReservationRoomFolioResponse chứa tất cả thông tin chi tiết về reservation room
     */
    ReservationRoomFolioResponse getReservationRoomFolio(Long reservationRoomId);

    /**
     * Trả phòng cho một reservation room cụ thể.
     * Áp dụng phí trả phòng muộn nếu có.
     * Cập nhật trạng thái phòng thành CHECKED_OUT và phòng vật lý thành DIRTY.
     * Cập nhật trạng thái đặt phòng thành CHECKED_OUT nếu tất cả các phòng đều đã trả phòng.
     *
     * @param reservationRoomId ID của reservation room cần trả phòng
     * @return ReservationRoomCheckOutResponse chứa thông tin về kết quả trả phòng.
     */
    ReservationRoomCheckOutResponse checkOutReservationRoom(Long reservationRoomId);

    /**
     * Xử lý thanh toán cho các mục folio đã chọn của một reservation room.
     * Backend sẽ tính toán số tiền cần thanh toán dựa trên các mục folio đã chọn và áp dụng khấu trừ đặt cọc nếu được yêu cầu.
     * Đối với phương thức VNPAY, response sẽ bao gồm một URL chuyển hướng được tạo sau khi tạo giao dịch thanh toán/đặt cọc.
     *
     * @param reservationRoomId ID của reservation room mà bạn muốn xử lý thanh toán
     * @param request           chi tiết thanh toán, bao gồm danh sách các folio, method thanh toán, số tiền đặt cọc muốn khấu trừ.
     * @return PaymentResponse chứa thông tin chi tiết về
     */
    // TODO: Cần thêm service xử lý khi khách hàng thanh toán tiền mặt tại quầy. Lễ tân sẽ Mark As Paid cho các mục folio đã chọn.
    PaymentResponse processPayment(Long reservationRoomId, PaymentRequest request);
}
