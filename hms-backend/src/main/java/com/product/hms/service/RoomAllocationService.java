package com.product.hms.service;

import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.entity.ReservationRoomEntity;
import com.product.hms.entity.RoomClassEntity;

import java.util.List;
import java.util.Map;

// TODO: Nên chuyển qua ReservationRoomService vì RoomAllocation nghe có vẻ không rõ
public interface RoomAllocationService {
    /**
     * Tạo các bản ghi ReservationRoomEntity dựa trên thông tin đặt phòng và số lượng phòng theo từng loại phòng được cung cấp trong request.
     *
     * @param reservation   bản ghi đặt phòng mà các bản ghi ReservationRoomEntity sẽ liên kết đến
     * @param request       đối tượng chứa thông tin chi tiết về đặt phòng, danh sách hạng phòng và số lượng người ở mỗi hạng phòng
     * @param roomClassById bản đồ ánh xạ từ ID hạng phòng đến thực thể RoomClassEntity, được sử dụng để xác thực và lấy thông tin hạng phòng khi tạo các bản ghi ReservationRoomEntity
     * @return danh sách các bản ghi ReservationRoomEntity đã được tạo.
     */
    // TODO: Cần xem xét lại Map<Long, RoomClassEntity> roomClassById có thể đang sai logic
    List<ReservationRoomEntity> createRoomAllocations(
            ReservationEntity reservation,
            ReservationRequest request,
            Map<Long, RoomClassEntity> roomClassById
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
