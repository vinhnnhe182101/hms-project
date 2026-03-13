package com.product.hms.service;

import com.product.hms.dto.request.ReservationCheckInRequest;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.dto.request.ReservationSearchFilter;
import com.product.hms.dto.response.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for reservation operations
 */
public interface ReservationService {

    /**
     * Tạo một đặt phòng mới dựa trên thông tin khách hàng và phòng được cung cấp trong request.
     *
     * @param request đối tượng chứa thông tin chi tiết về đặt phòng.
     * @return ReservationResponse chứa thông tin về đặt phòng đã được tạo.
     */
    ReservationResponse createReservation(ReservationRequest request);

    /**
     * Cập nhật một đặt phòng hiện có dựa trên reservationId và thông tin mới được cung cấp trong request.
     *
     * @param reservationId ID của đặt phòng cần cập nhật.
     * @param request       đối tượng chứa thông tin mới để cập nhật đặt phòng.
     * @return ReservationResponse chứa thông tin về đặt phòng đã được cập nhật.
     */
    ReservationResponse updateReservation(Long reservationId, ReservationRequest request);

    /**
     * Hủy một đặt phòng hiện có dựa trên reservationId. Áp dụng chính sách hủy phòng: chỉ hoàn tiền đặt cọc nếu hủy trước >24h so với thời gian check-in.
     *
     * @param reservationId ID của đặt phòng cần hủy.
     * @return ReservationResponse chứa thông tin về đặt phòng đã được hủy, với trạng thái CANCELED.
     */
    ReservationResponse cancelReservation(Long reservationId);

    /**
     * Check in một đặt phòng và gán phòng vật lý cho mỗi reservation room.
     *
     * @param reservationId ID của đặt phòng cần check-in.
     * @param request       đối tượng chứa thông tin chi tiết về check-in, bao gồm tùy chọn gán phòng thủ công hoặc tự động.
     * @return ReservationResponse chứa thông tin về đặt phòng đã được check-in, với trạng thái IN_HOUSE.
     */
    ReservationResponse checkInReservation(Long reservationId, ReservationCheckInRequest request);

    /**
     * Search reservations with filter and pagination.
     *
     * @param filter  filter DTO (guestName, status, checkInDateFrom, checkInDateTo)
     * @param pageable Spring Data pageable
     * @return paged result
     */
    Page<ReservationResponse> search(ReservationSearchFilter filter, Pageable pageable);
}
