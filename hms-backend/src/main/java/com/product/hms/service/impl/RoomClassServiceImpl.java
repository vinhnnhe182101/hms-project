package com.product.hms.service.impl;

import com.product.hms.dto.request.UpsertRoomClassRequest;
import com.product.hms.dto.response.AssetResponse;
import com.product.hms.dto.response.RoomClassDetailResponse;
import com.product.hms.dto.response.RoomClassResponse;
import com.product.hms.dto.response.RoomImgResponse;
import com.product.hms.entity.RoomAssetEntity;
import com.product.hms.entity.RoomClassEntity;
import com.product.hms.entity.RoomImgEntity;
import com.product.hms.enums.ReservationStatus;
import com.product.hms.exception.BadRequestException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.*;
import com.product.hms.service.RoomClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomClassServiceImpl implements RoomClassService {

    private final RoomClassRepository roomClassRepository;
    private final RoomImgRepository roomImgRepository;
    private final RatingRepository ratingRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<RoomClassResponse> getRoomClassList(LocalDateTime checkIn, LocalDateTime checkOut, int page, int size, String sortBy) {
        if (checkIn == null) {
            checkIn = LocalDateTime.now();
        }
        if (checkOut == null) {
            checkOut = checkIn.plusDays(1);
        }

        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            checkOut = checkIn.plusDays(1);
        }

        Sort sort = Sort.by("id").ascending();
        if ("price_asc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by("basePrice").ascending();
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sort = Sort.by("basePrice").descending();
        }

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);

        List<ReservationStatus> statuses = List.of(
                ReservationStatus.PENDING_DEPOSIT,
                ReservationStatus.CONFIRMED,
                ReservationStatus.IN_HOUSE
        );
        return roomClassRepository.findRoomClassSummary(Timestamp.valueOf(checkIn), Timestamp.valueOf(checkOut), statuses, pageable)
                .map(this::mapSummaryToResponse);
    }

    @Override
    public List<RoomClassResponse> getAllRoomClasses() {
        return roomClassRepository.findAll()
                .stream()
                .filter(RoomClassEntity::getIsActive)
                .map(this::mapEntityToResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public RoomClassDetailResponse getRoomClassDetail(Long id) {
        List<Object[]> rows = roomClassRepository.findDetailById(id);
        if (rows.isEmpty()) {
            throw new RuntimeException("Không tìm thấy loại phòng với ID: " + id);
        }
        Object[] row = rows.get(0);

        List<RoomImgResponse> images = buildAllImages(id);

        List<RoomAssetEntity> roomAssets = roomClassRepository.findAssetsByRoomClassId(id);
        List<AssetResponse> assets = roomAssets.stream()
                .collect(Collectors.toMap(
                        ra -> ra.getAssetEntity().getName(),
                        ra -> ra,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .map(ra -> AssetResponse.builder()
                        .id(ra.getAssetEntity().getId())
                        .name(ra.getAssetEntity().getName())
                        .build())
                .toList();

        Double avgRating = ratingRepository.getAverageRatingByRoomClassId(((Number) row[0]).longValue());

        return RoomClassDetailResponse.builder()
                .id(((Number) row[0]).longValue())
                .name((String) row[1])
                .standardCapacity(((Number) row[2]).intValue())
                .maxCapacity(((Number) row[3]).intValue())
                .basePrice((BigDecimal) row[4])
                .extraPersonFee((BigDecimal) row[5])
                .totalRooms(row[6] != null ? ((Number) row[6]).longValue() : 0L)
                .images(images)
                .assets(assets)
                .averageRating(avgRating)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomClassResponse> getOtherRoomClasses(Long excludeId) {
        return roomClassRepository.findOtherRoomClasses(excludeId)
                .stream()
                .limit(3)
                .map(this::mapSummaryToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomClassResponse> getAllRoomClasses(Pageable pageable) {
        return roomClassRepository.findRoomClassSummaryWithoutDate(pageable)
                .map(this::mapSummaryToResponse);
    }

    @Override
    @Transactional
    public Page<RoomClassResponse> getRoomClassesForAdmin(Pageable pageable) {
        syncLegacyRoomTypesToRoomClasses();
        return roomClassRepository.findAllByIsActiveTrue(pageable)
                .map(this::mapEntityToResponse);
    }

    private void syncLegacyRoomTypesToRoomClasses() {
        roomTypeRepository.findAll().forEach(roomType -> {
            String typeName = roomType.getTypeName() == null ? null : roomType.getTypeName().trim();
            if (typeName == null || typeName.isBlank()) {
                return;
            }

            roomClassRepository.findByNameIgnoreCaseAndIsActiveTrue(typeName)
                    .orElseGet(() -> {
                        RoomClassEntity entity = new RoomClassEntity();
                        entity.setName(typeName);
                        entity.setStandardCapacity(roomType.getStandardOccupancy());
                        entity.setMaxCapacity(roomType.getMaxOccupancy());
                        entity.setBasePrice(roomType.getBaseRatePerNight());
                        entity.setExtraPersonFee(BigDecimal.ZERO);
                        entity.setIsActive(true);
                        return roomClassRepository.save(entity);
                    });
        });
    }

    @Override
    @Transactional
    public RoomClassResponse createRoomClass(UpsertRoomClassRequest request) {
        String name = request.name() == null ? null : request.name().trim();
        validateUpsertRequest(name, request.standardCapacity(), request.maxCapacity());

        if (roomClassRepository.existsByNameIgnoreCaseAndIsActiveTrue(name)) {
            throw new BadRequestException(ErrorCode.INVALID_DATA, "Room type name already exists: " + name);
        }

        RoomClassEntity entity = new RoomClassEntity();
        entity.setName(name);
        entity.setStandardCapacity(request.standardCapacity());
        entity.setMaxCapacity(request.maxCapacity());
        entity.setBasePrice(request.basePrice());
        entity.setExtraPersonFee(request.extraPersonFee());
        entity.setIsActive(true);

        RoomClassEntity saved = roomClassRepository.save(entity);
        return mapEntityToResponse(saved);
    }

    @Override
    @Transactional
    public RoomClassResponse updateRoomClass(Long id, UpsertRoomClassRequest request) {
        RoomClassEntity entity = roomClassRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROOM_CLASS_NOT_FOUND, "Room type not found with id: " + id));

        String name = request.name() == null ? null : request.name().trim();
        validateUpsertRequest(name, request.standardCapacity(), request.maxCapacity());

        if (roomClassRepository.existsByNameIgnoreCaseAndIsActiveTrueAndIdNot(name, id)) {
            throw new BadRequestException(ErrorCode.INVALID_DATA, "Room type name already exists: " + name);
        }

        entity.setName(name);
        entity.setStandardCapacity(request.standardCapacity());
        entity.setMaxCapacity(request.maxCapacity());
        entity.setBasePrice(request.basePrice());
        entity.setExtraPersonFee(request.extraPersonFee());

        RoomClassEntity saved = roomClassRepository.save(entity);
        return mapEntityToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteRoomClass(Long id) {
        RoomClassEntity entity = roomClassRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROOM_CLASS_NOT_FOUND, "Room type not found with id: " + id));

        entity.setIsActive(false);
        roomClassRepository.save(entity);
    }


    private RoomClassResponse mapSummaryToResponse(Object[] row) {
        Long roomClassId = ((Number) row[0]).longValue();
        Double avgRating = ratingRepository.getAverageRatingByRoomClassId(roomClassId);
        return RoomClassResponse.builder()
                .id(roomClassId)
                .name((String) row[1])
                .standardCapacity(((Number) row[2]).intValue())
                .maxCapacity(((Number) row[3]).intValue())
                .basePrice((BigDecimal) row[4])
                .extraPersonFee((BigDecimal) row[5])
                .primaryImage(buildPrimaryImage(roomClassId))
                .totalRooms(row[6] != null ? ((Number) row[6]).longValue() : 0L)
                .averageRating(avgRating != null ? avgRating : 0.0)
                .build();
    }

    private List<RoomImgResponse> buildAllImages(Long roomClassId) {
        return roomImgRepository
                .findAllByRoomClassEntityIdOrderByIsPrimaryDescIdAsc(roomClassId)
                .stream()
                .map(img -> {
                    String dataUrl = null;
                    if (img.getImgData() != null) {
                        String base64 = Base64.getEncoder().encodeToString(img.getImgData());
                        dataUrl = "data:" + img.getImgType() + ";base64," + base64;
                    }
                    return RoomImgResponse.builder()
                            .id(img.getId())
                            .dataUrl(dataUrl)
                            .imgType(img.getImgType())
                            .isPrimary(img.getIsPrimary())
                            .build();
                })
                .toList();
    }

    private RoomImgResponse buildPrimaryImage(Long roomClassId) {
        Optional<RoomImgEntity> imgOpt =
                roomImgRepository.findFirstByRoomClassEntityIdAndIsPrimaryTrue(roomClassId);

        return imgOpt.map(img -> {
            String dataUrl = null;
            if (img.getImgData() != null) {
                String base64 = Base64.getEncoder().encodeToString(img.getImgData());
                dataUrl = "data:" + img.getImgType() + ";base64," + base64;
            }
            return RoomImgResponse.builder()
                    .id(img.getId())
                    .dataUrl(dataUrl)
                    .imgType(img.getImgType())
                    .isPrimary(img.getIsPrimary())
                    .build();
        }).orElse(null);
    }

    private void validateUpsertRequest(String name, Integer standardCapacity, Integer maxCapacity) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException(ErrorCode.INVALID_DATA, "Room type name is required");
        }

        if (standardCapacity == null || standardCapacity < 1) {
            throw new BadRequestException(ErrorCode.INVALID_DATA, "Standard occupancy must be at least 1");
        }

        if (maxCapacity == null || maxCapacity < standardCapacity) {
            throw new BadRequestException(
                    ErrorCode.INVALID_DATA,
                    "Max occupancy must be greater than or equal to standard occupancy"
            );
        }
    }

    private RoomClassResponse mapEntityToResponse(RoomClassEntity entity) {
        return RoomClassResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .standardCapacity(entity.getStandardCapacity())
                .maxCapacity(entity.getMaxCapacity())
                .basePrice(entity.getBasePrice())
                .extraPersonFee(entity.getExtraPersonFee())
                .primaryImage(null)
                .totalRooms(0L)
                .averageRating(0.0)
                .build();
    }
}
