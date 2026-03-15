package com.product.hms.api;

import com.product.hms.dto.request.UpsertRoomTypeRequest;
import com.product.hms.dto.response.AdminRoomTypeResponse;
import com.product.hms.entity.RoomTypeEntity;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.RoomTypeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/room-types")
@RequiredArgsConstructor
public class RoomTypeApi {

    private final RoomTypeRepository roomTypeRepository;

    @GetMapping
    public ResponseEntity<Page<AdminRoomTypeResponse>> getRoomTypes(Pageable pageable) {
        Pageable sanitizedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<AdminRoomTypeResponse> response = roomTypeRepository.findAll(sanitizedPageable)
                .map(this::toResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AdminRoomTypeResponse> createRoomType(@Valid @RequestBody UpsertRoomTypeRequest request) {
        String typeName = request.typeName().trim();
        validateOccupancy(request.standardOccupancy(), request.maxOccupancy());

        if (roomTypeRepository.existsByTypeNameIgnoreCase(typeName)) {
            throw new BadRequestException(ErrorCode.INVALID_DATA, "Room type name already exists: " + typeName);
        }

        RoomTypeEntity entity = new RoomTypeEntity();
        entity.setTypeName(typeName);
        entity.setStandardOccupancy(request.standardOccupancy());
        entity.setMaxOccupancy(request.maxOccupancy());
        entity.setBaseRatePerNight(request.baseRatePerNight());

        return ResponseEntity.ok(toResponse(roomTypeRepository.save(entity)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminRoomTypeResponse> updateRoomType(
            @PathVariable Long id,
            @Valid @RequestBody UpsertRoomTypeRequest request
    ) {
        RoomTypeEntity entity = roomTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Room type not found with id: " + id));

        String typeName = request.typeName().trim();
        validateOccupancy(request.standardOccupancy(), request.maxOccupancy());

        if (roomTypeRepository.existsByTypeNameIgnoreCaseAndIdNot(typeName, id)) {
            throw new BadRequestException(ErrorCode.INVALID_DATA, "Room type name already exists: " + typeName);
        }

        entity.setTypeName(typeName);
        entity.setStandardOccupancy(request.standardOccupancy());
        entity.setMaxOccupancy(request.maxOccupancy());
        entity.setBaseRatePerNight(request.baseRatePerNight());

        return ResponseEntity.ok(toResponse(roomTypeRepository.save(entity)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new NotFoundException(ErrorCode.RESOURCE_NOT_FOUND, "Room type not found with id: " + id);
        }

        roomTypeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void validateOccupancy(Integer standardOccupancy, Integer maxOccupancy) {
        if (maxOccupancy < standardOccupancy) {
            throw new BadRequestException(
                    ErrorCode.INVALID_DATA,
                    "Max occupancy must be greater than or equal to standard occupancy"
            );
        }
    }

    private AdminRoomTypeResponse toResponse(RoomTypeEntity entity) {
        return AdminRoomTypeResponse.builder()
                .id(entity.getId())
                .typeName(entity.getTypeName())
                .standardOccupancy(entity.getStandardOccupancy())
                .maxOccupancy(entity.getMaxOccupancy())
                .baseRatePerNight(entity.getBaseRatePerNight())
                .build();
    }
}
