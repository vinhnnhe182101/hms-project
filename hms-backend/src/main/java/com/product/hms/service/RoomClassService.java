package com.product.hms.service;

import com.product.hms.dto.request.UpsertRoomClassRequest;
import com.product.hms.dto.response.RoomClassDetailResponse;
import com.product.hms.dto.response.RoomClassResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomClassService {

    Page<RoomClassResponse> getRoomClassList(LocalDateTime checkIn, LocalDateTime checkOut, int page, int size, String sortBy);

    List<RoomClassResponse> getAllRoomClasses();

    Page<RoomClassResponse> getAllRoomClasses(Pageable pageable);

    RoomClassDetailResponse getRoomClassDetail(Long id);

    List<RoomClassResponse> getOtherRoomClasses(Long excludeId);

    Page<RoomClassResponse> getRoomClassesForAdmin(Pageable pageable);

    RoomClassResponse createRoomClass(UpsertRoomClassRequest request);

    RoomClassResponse updateRoomClass(Long id, UpsertRoomClassRequest request);

    void deleteRoomClass(Long id);
}
