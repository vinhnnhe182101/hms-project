package com.product.hms.service;

import com.product.hms.dto.RoomTypeCreateDTO;
import com.product.hms.dto.RoomTypeResponseDTO;

import java.util.List;

public interface RoomTypeService {
    RoomTypeResponseDTO create(RoomTypeCreateDTO dto);
    RoomTypeResponseDTO update(Long id, RoomTypeCreateDTO dto);
    RoomTypeResponseDTO findById(Long id);
    List<RoomTypeResponseDTO> findAllActive();
    void delete(Long id);   // soft delete
}