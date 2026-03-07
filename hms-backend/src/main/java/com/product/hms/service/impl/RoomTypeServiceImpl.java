package com.product.hms.service.impl;

import com.product.hms.dto.RoomTypeCreateDTO;
import com.product.hms.dto.RoomTypeResponseDTO;
import com.product.hms.entity.RoomType;
import com.product.hms.exception.BusinessException;
import com.product.hms.repository.RoomTypeRepository;
import com.product.hms.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    @Override
    @Transactional
    public RoomTypeResponseDTO create(RoomTypeCreateDTO dto) {
        if (roomTypeRepository.existsByName(dto.getName())) {
            throw new BusinessException("Tên loại phòng đã tồn tại");
        }

        if (dto.getMaxCapacity() < dto.getStandardCapacity()) {
            throw new BusinessException("Sức chứa tối đa phải lớn hơn hoặc bằng sức chứa tiêu chuẩn");
        }

        RoomType entity = RoomType.builder()
                .name(dto.getName())
                .basePrice(dto.getBasePrice())
                .standardCapacity(dto.getStandardCapacity())
                .maxCapacity(dto.getMaxCapacity())
                .extraPersonFee(dto.getExtraPersonFee())
                .description(dto.getDescription())
                .build();

        entity = roomTypeRepository.save(entity);

        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public RoomTypeResponseDTO update(Long id, RoomTypeCreateDTO dto) {
        RoomType entity = roomTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy loại phòng"));

        if (!entity.getName().equals(dto.getName()) &&
                roomTypeRepository.existsByName(dto.getName())) {
            throw new BusinessException("Tên loại phòng đã tồn tại");
        }

        entity.setName(dto.getName());
        entity.setBasePrice(dto.getBasePrice());
        entity.setStandardCapacity(dto.getStandardCapacity());
        entity.setMaxCapacity(dto.getMaxCapacity());
        entity.setExtraPersonFee(dto.getExtraPersonFee());
        entity.setDescription(dto.getDescription());

        entity = roomTypeRepository.save(entity);
        return mapToResponse(entity);
    }

    @Override
    public RoomTypeResponseDTO findById(Long id) {
        RoomType entity = roomTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy loại phòng"));
        return mapToResponse(entity);
    }

    @Override
    public List<RoomTypeResponseDTO> findAllActive() {
        return roomTypeRepository.findAll().stream()
                .filter(RoomType::isActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RoomType entity = roomTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy loại phòng"));

        if (!entity.getRooms().isEmpty()) {
            throw new BusinessException("Không thể xóa loại phòng đang có phòng liên kết");
        }

        entity.setActive(false);
        roomTypeRepository.save(entity);
    }

    private RoomTypeResponseDTO mapToResponse(RoomType entity) {
        RoomTypeResponseDTO dto = new RoomTypeResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setBasePrice(entity.getBasePrice());
        dto.setStandardCapacity(entity.getStandardCapacity());
        dto.setMaxCapacity(entity.getMaxCapacity());
        dto.setExtraPersonFee(entity.getExtraPersonFee());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.isActive());
        dto.setRoomCount(entity.getRooms().size());
        return dto;
    }
}