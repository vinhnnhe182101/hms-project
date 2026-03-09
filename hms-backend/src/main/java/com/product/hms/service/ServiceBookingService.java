package com.product.hms.service;

import com.product.hms.dto.request.ServiceBookingRequest;
import com.product.hms.dto.request.UpdateServiceBookingRequest;
import com.product.hms.dto.response.ServiceBookingResponse;

/**
 * Service interface for service booking operations
 */
public interface ServiceBookingService {

    /**
     * Tao một booking dịch vụ mới cho một reservation room. Chỉ được phép khi trạng thái của reservation là CHECKED_IN.
     *
     * @param reservationRoomId     là id của reservation room mà booking dịch vụ sẽ được tạo cho nó
     * @param serviceBookingRequest là đối tượng chứa thông tin chi tiết về booking dịch vụ cần tạo, bao gồm id của dịch vụ và số lượng
     * @return ServiceBookingResponse chứa thông tin về booking dịch vụ đã được tạo
     */
    ServiceBookingResponse createServiceBooking(Long reservationRoomId, ServiceBookingRequest serviceBookingRequest);

    /**
     * Cập nhật một booking dịch vụ (chỉ được phép khi trạng thái là PENDING).
     * Chỉ có thể thay đổi số lượng.
     *
     * @param reservationRoomId là id của reservation room mà booking dịch vụ cần được cập nhật thuộc về nó
     * @param serviceBookingId  là id của booking dịch vụ cần được cập nhật
     * @param request           là đối tượng chứa thông tin chi tiết về cập nhật booking dịch vụ, chỉ bao gồm số lượng
     * @return ServiceBookingResponse chứa thông tin về booking dịch vụ đã được cập nhật
     */
    ServiceBookingResponse updateServiceBooking(Long reservationRoomId, Long serviceBookingId, UpdateServiceBookingRequest request);

    /**
     * Hủy một booking dịch vụ (chỉ được phép khi trạng thái là PENDING).
     *
     * @param reservationRoomId là id của reservation room mà booking dịch vụ cần được hủy thuộc về nó
     * @param serviceBookingId  là id của booking dịch vụ cần được hủy
     * @return ServiceBookingResponse chứa thông tin về booking dịch vụ đã được hủy
     */
    ServiceBookingResponse cancelServiceBooking(Long reservationRoomId, Long serviceBookingId);
}
