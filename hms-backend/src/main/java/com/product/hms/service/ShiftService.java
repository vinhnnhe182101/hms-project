package com.product.hms.service;

import com.product.hms.dto.request.ShiftRequest;
import com.product.hms.dto.response.ShiftResponse;

import java.util.List;

public interface ShiftService {

    ShiftResponse createShift(ShiftRequest request);

    ShiftResponse updateShift(Long id, ShiftRequest request);

    ShiftResponse getShiftById(Long id);

    List<ShiftResponse> getAllShifts();

    List<ShiftResponse> getAllActiveShifts();

    void deleteShift(Long id);
}
