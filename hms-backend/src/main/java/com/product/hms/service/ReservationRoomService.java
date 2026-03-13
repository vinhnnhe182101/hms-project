package com.product.hms.service;

import com.product.hms.dto.request.PaymentRequest;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.request.RoomChangeRequest;
import com.product.hms.dto.response.PaymentResponse;
import com.product.hms.dto.response.ReservationRoomCheckOutResponse;
import com.product.hms.dto.response.ReservationRoomFolioResponse;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;

import java.util.List;

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
    PaymentResponse processPayment(Long reservationRoomId, PaymentRequest request);

    /**
     * Chuyển phòng cho khách đã check-in.
     *
     * @param reservationRoomId ID của reservation room cần chuyển phòng
     * @param request           Thông tin chuyển phòng
     */
    void changeRoom(Long reservationRoomId, RoomChangeRequest request);

    /**
     * Tạo các bản ghi ReservationRoomEntity dựa trên thông tin đặt phòng và số lượng phòng theo từng loại phòng được cung cấp trong request.
     * Service sẽ tự lấy RoomClassEntity từ repository dựa trên roomClassId trong request.
     *
     * @param reservation bản ghi đặt phòng mà các bản ghi ReservationRoomEntity sẽ liên kết đến
     * @param request     đối tượng chứa thông tin chi tiết về đặt phòng, danh sách hạng phòng và số lượng người ở mỗi hạng phòng
     * @return danh sách các bản ghi ReservationRoomEntity đã được tạo.
     */
    List<ReservationRoomEntity> createRoomAllocations(
            ReservationEntity reservation,
            ReservationRequest request
    );

    /**
     * Lấy danh sách các bản ghi ReservationRoomEntity liên quan đến một ReservationEntity cụ thể.
     *
     * @param reservation bản ghi đặt phòng mà bạn muốn lấy các bản ghi ReservationRoomEntity liên quan đến nó
     * @return danh sách các bản ghi ReservationRoomEntity liên quan đến ReservationEntity đã cho.
     */
    List<ReservationRoomEntity> getAllocationsByReservation(ReservationEntity reservation);

    /**
     * Xóa tất cả các bản ghi ReservationRoomEntity liên quan đến một ReservationEntity cụ thể.
     *
     * @param reservation bản ghi đặt phòng mà bạn muốn xóa tất cả các bản ghi ReservationRoomEntity liên quan đến nó
     */
    void deleteAllocationsByReservation(ReservationEntity reservation);
}
