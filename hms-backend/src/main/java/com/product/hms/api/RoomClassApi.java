package com.product.hms.api;

import com.product.hms.dto.response.RoomClassDetailResponse;
import com.product.hms.dto.response.RoomClassResponse;
import com.product.hms.service.RoomClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/room-classes")
@RequiredArgsConstructor
public class RoomClassApi {

    private final RoomClassService roomClassService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoomClassList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<RoomClassResponse> resultPage = roomClassService.getRoomClassList(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("data", resultPage.getContent());
        response.put("currentPage", resultPage.getNumber());
        response.put("totalItems", resultPage.getTotalElements());
        response.put("totalPages", resultPage.getTotalPages());
        response.put("pageSize", resultPage.getSize());
        response.put("isLast", resultPage.isLast());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/room-classes/{id}
     *
     * Chi tiết một loại phòng gồm:
     * - Tên, sức chứa tiêu chuẩn/tối đa, giá cơ bản, phụ phí
     * - Tổng số phòng
     * - Ảnh chính
     * - Danh sách tài sản (assets) trong phòng
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomClassDetailResponse> getRoomClassDetail(@PathVariable Long id) {
        return ResponseEntity.ok(roomClassService.getRoomClassDetail(id));
    }

    /**
     * GET /api/v1/room-classes/{id}/others
     *
     * Lấy danh sách các loại phòng khác (trừ loại phòng có ID hiện tại).
     * Dùng để hiển thị gợi ý "Loại phòng khác" trên trang detail.
     */
    @GetMapping("/{id}/others")
    public ResponseEntity<List<RoomClassResponse>> getOtherRoomClasses(@PathVariable Long id) {
        return ResponseEntity.ok(roomClassService.getOtherRoomClasses(id));
    }
}
