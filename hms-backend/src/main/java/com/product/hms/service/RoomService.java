package com.product.hms.service;

import com.product.hms.dto.request.CreateRoomRequest;
import com.product.hms.dto.request.RoomSearchFilter;
import com.product.hms.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service interface for room operations
 */
public interface RoomService {
    /**
     * Tìm kiếm phòng dựa trên các tiêu chí tìm kiếm được cung cấp
     *
     * @param filter   Các tiêu chí tìm kiếm
     * @param pageable Thông tin phân trang
     * @return Trang kết quả tìm kiếm phòng
     */
    Page<RoomResponse> search(RoomSearchFilter filter, Pageable pageable);

    RoomResponse createRoom(CreateRoomRequest request);

    RoomResponse updateRoom(Long id, CreateRoomRequest request);

    void deleteRoom(Long id);

    /**
     * Lấy thông tin về số lượng phòng trống theo từng hạng phòng cho khoảng thời
     * gian từ check-in đến check-out.
     *
     * @param checkInDate  Ngày giờ check-in dự kiến
     * @param checkOutDate Ngày giờ check-out dự kiến
     * @return Danh sách RoomClassAvailabilityResponse, mỗi phần tử chứa thông tin
     *         về một hạng phòng và số lượng phòng trống thuộc hạng đó.
     */
    List<RoomClassAvailabilityResponse> getAvailableRooms(Timestamp checkInDate, Timestamp checkOutDate);

    /**
     * Lấy thông tin về các phòng trống theo từng hạng phòng cho khoảng thời gian từ
     * check-in đến check-out.
     *
     * @param checkInDate  Ngày giờ check-in dự kiến
     * @param checkOutDate Ngày giờ check-out dự kiến
     * @return Danh sách RoomClassAvailableRoomsResponse, mỗi phần tử chứa thông tin
     *         về một hạng phòng và danh sách các phòng trống thuộc hạng đó.
     */
    List<RoomClassAvailableRoomsResponse> getAvailableRoomsForAssignment(Timestamp checkInDate, Timestamp checkOutDate);

    /**
     * Lấy thông tin về các phòng trống thuộc một hạng phòng cụ thể cho khoảng thời
     * gian từ check-in đến check-out.
     *
     * @param roomClassId  ID của hạng phòng cần lấy thông tin
     * @param checkInDate  Ngày giờ check-in dự kiến
     * @param checkOutDate Ngày giờ check-out dự kiến
     * @return Danh sách AvailableRoomResponse chứa id và số phòng
     */
    List<AvailableRoomResponse> getAvailableRoomsByRoomClassIdForAssignment(
            Long roomClassId,
            Timestamp checkInDate,
            Timestamp checkOutDate);

    /**
     * Lấy tất cả các tầng có phòng trống từ các phòng đang hoạt động.
     *
     * @return Danh sách các tầng có phòng trống (ví dụ: ["1", "2", "3", "12"])
     */
    List<String> getAvailableFloors();

    /**
     * Lấy ma trận trạng thái phòng cho một tầng cụ thể.
     *
     * @param floor là định danh tầng (ví dụ: "1", "12").
     * @return Danh sách RoomMatrixResponse chứa thông tin về các phòng trên tầng đã
     *         chỉ định.
     */
    List<RoomMatrixResponse> getRoomStatusMatrixByFloor(String floor);
}
