package com.product.hms.service.impl;

import com.product.hms.dto.request.ShiftRequest;
import com.product.hms.dto.response.ShiftResponse;
import com.product.hms.entity.ShiftEntity;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.ShiftRepository;
import com.product.hms.service.ShiftService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;

    public ShiftServiceImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    @Transactional
    public ShiftResponse createShift(ShiftRequest request) {
        ShiftEntity shift = new ShiftEntity();
        shift.setShiftName(request.getShiftName());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setIsActive(true);

        ShiftEntity savedShift = shiftRepository.save(shift);
        return mapToResponse(savedShift);
    }

    @Override
    @Transactional
    public ShiftResponse updateShift(Long id, ShiftRequest request) {
        ShiftEntity shift = shiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHIFT_NOT_FOUND, "Shift not found with id " + id));

        shift.setShiftName(request.getShiftName());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());

        ShiftEntity updatedShift = shiftRepository.save(shift);
        return mapToResponse(updatedShift);
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftResponse getShiftById(Long id) {
        ShiftEntity shift = shiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHIFT_NOT_FOUND, "Shift not found with id " + id));
        return mapToResponse(shift);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftResponse> getAllShifts() {
        return shiftRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftResponse> getAllActiveShifts() {
        return shiftRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteShift(Long id) {
        ShiftEntity shift = shiftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHIFT_NOT_FOUND, "Shift not found with id " + id));

        shift.setIsActive(false);
        shiftRepository.save(shift);
    }

    private ShiftResponse mapToResponse(ShiftEntity shift) {
        return ShiftResponse.builder()
                .id(shift.getId())
                .shiftName(shift.getShiftName())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .isActive(shift.getIsActive())
                .build();
    }
}
