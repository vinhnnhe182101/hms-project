package com.product.hms.service.impl;

import com.product.hms.dto.response.*;
import com.product.hms.entity.RoomAssetEntity;
import com.product.hms.entity.RoomImgEntity;
import com.product.hms.repository.RoomClassRepository;
import com.product.hms.repository.RoomImgRepository;
import com.product.hms.repository.RatingRepository;
import com.product.hms.service.RoomClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Override
    @Transactional(readOnly = true)
    public Page<RoomClassResponse> getRoomClassList(Pageable pageable) {
        return roomClassRepository.findRoomClassSummary(pageable)
                .map(this::mapSummaryToResponse);
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


    private RoomClassResponse mapSummaryToResponse(Object[] row) {
        Long roomClassId = ((Number) row[0]).longValue();
        Double avgRating = ratingRepository.getAverageRatingByRoomClassId(roomClassId);
        return RoomClassResponse.builder()
                .id(roomClassId)
                .name((String) row[1])
                .standardCapacity(((Number) row[2]).intValue())
                .basePrice((BigDecimal) row[3])
                .primaryImage(buildPrimaryImage(roomClassId))
                .totalRooms(row[4] != null ? ((Number) row[4]).longValue() : 0L)
                .averageRating(avgRating)
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
}
