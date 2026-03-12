package com.product.hms.service;

import com.product.hms.dto.request.RoomSearchFilter;
import com.product.hms.dto.response.RoomClassAvailableRoomsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomSearchService {
    Page<RoomClassAvailableRoomsResponse> search(RoomSearchFilter filter, Pageable pageable);
}

