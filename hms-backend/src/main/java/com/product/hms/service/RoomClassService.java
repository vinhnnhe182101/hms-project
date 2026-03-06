package com.product.hms.service;

import com.product.hms.dto.response.RoomClassDetailResponse;
import com.product.hms.dto.response.RoomClassResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomClassService {

    Page<RoomClassResponse> getRoomClassList(Pageable pageable);

    /**
     * Lấy thông tin chi tiết một loại phòng:
     * tên, sức chứa, giá, phụ phí, số phòng, ảnh, danh sách tài sản.
     */
    RoomClassDetailResponse getRoomClassDetail(Long id);

    /**
     * Lấy danh sách các loại phòng khác (dùng để gợi ý trên trang detail).
     */
    List<RoomClassResponse> getOtherRoomClasses(Long excludeId);
}
