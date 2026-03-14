// service/impl/housekeeping/MinibarServiceImpl.java
package com.product.hms.service.impl.housekeeping;

import com.product.hms.dto.request.MinibarConsumptionRequest;
import com.product.hms.dto.response.MinibarConsumptionResponse;
import com.product.hms.dto.response.MinibarItemResponse;
import com.product.hms.entity.*;
import com.product.hms.enums.FolioItemStatus;
import com.product.hms.enums.FolioItemType;
import com.product.hms.exception.BadRequest;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.ResourceNotFoundException;
import com.product.hms.repository.*;
import com.product.hms.service.housekeeping.MinibarService;
import com.product.hms.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MinibarServiceImpl implements MinibarService {

    private final RoomAssetRepository roomAssetRepository;
    private final ReservationRoomRepository reservationRoomRepository;
    private final FolioRepository folioRepository;
    private final FolioItemRepository folioItemRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional(readOnly = true)
    public List<MinibarItemResponse> getRoomMinibarItems(Long roomId) {
        log.info("Fetching minibar items for room: {}", roomId);

        // Lấy tất cả room assets của phòng, filter category là minibar
        List<RoomAssetEntity> assets = roomAssetRepository.findByRoomEntityId(roomId).stream()
                .filter(asset -> isMinibarItem(asset.getAssetEntity()))
                .filter(asset -> asset.getQuantity() > 0)
                .collect(Collectors.toList());

        return assets.stream()
                .map(this::convertToItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MinibarConsumptionResponse> reportConsumption(MinibarConsumptionRequest request) {
        StaffEntity currentStaff = securityUtil.getCurrentStaff();
        log.info("Staff {} reporting minibar consumption for room: {}",
                currentStaff.getFullName(), request.getRoomId());

        // Find current reservation
        ReservationRoomEntity reservationRoom = reservationRoomRepository
                .findCurrentReservationByRoomId(request.getRoomId())
                .orElseThrow(() -> new BadRequest(
                        ErrorCode.RESERVATION_ROOM_NOT_FOUND.code() +
                                ": No active reservation found for this room"));

        // Get folio
        FolioEntity folio = folioRepository
                .findByReservationRoomEntityId(reservationRoom.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.FOLIO_NOT_FOUND.code() + ": Folio not found"));

        List<MinibarConsumptionResponse> responses = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        StringBuilder description = new StringBuilder("Minibar: ");

        for (MinibarConsumptionRequest.MinibarItem item : request.getItems()) {
            if (item.getQuantity() <= 0) continue;

            RoomAssetEntity roomAsset = roomAssetRepository.findById(item.getRoomAssetId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            ErrorCode.ROOM_ASSET_NOT_FOUND.code() + ": Asset not found"));

            // Check if it's minibar item
            if (!isMinibarItem(roomAsset.getAssetEntity())) {
                throw new BadRequest("Item is not a minibar item");
            }

            // Check quantity
            if (roomAsset.getQuantity() < item.getQuantity()) {
                throw new BadRequest(
                        ErrorCode.INSUFFICIENT_ASSET_QUANTITY.code() +
                                String.format(": Not enough %s. Available: %d, Requested: %d",
                                        roomAsset.getAssetEntity().getName(),
                                        roomAsset.getQuantity(),
                                        item.getQuantity())
                );
            }

            // Update quantity
            roomAsset.setQuantity(roomAsset.getQuantity() - item.getQuantity());
            roomAssetRepository.save(roomAsset);

            // Calculate total
            BigDecimal price = roomAsset.getAssetEntity().getPrice();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            if (responses.size() > 0) {
                description.append(", ");
            }
            description.append(String.format("%s x%d ($%.2f)",
                    roomAsset.getAssetEntity().getName(),
                    item.getQuantity(),
                    itemTotal));

            responses.add(MinibarConsumptionResponse.builder()
                    .roomNumber(reservationRoom.getRoomEntity().getRoomNumber())
                    .assetName(roomAsset.getAssetEntity().getName())
                    .quantity(item.getQuantity())
                    .price(roomAsset.getAssetEntity().getPrice())
                    .total(itemTotal.doubleValue())
                    .status("ADDED_TO_FOLIO")
                    .createdAt(Timestamp.from(Instant.now()))
                    .build());

            log.info("Consumed {} x {} = ${}",
                    item.getQuantity(),
                    roomAsset.getAssetEntity().getName(),
                    itemTotal);
        }

        // Add to folio
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            FolioItemEntity folioItem = new FolioItemEntity();
            folioItem.setFolioEntity(folio);
            folioItem.setType(FolioItemType.MINIBAR_CHARGE);
            folioItem.setDescription(description.toString());
            folioItem.setQuantity(responses.size());
            folioItem.setTotalPrice(totalAmount);
            folioItem.setStatus(FolioItemStatus.UNPAID);
            folioItemRepository.save(folioItem);

            // Update folio
            folio.setTotalCharges(folio.getTotalCharges().add(totalAmount));
            folio.setBalance(folio.getBalance().add(totalAmount));
            folioRepository.save(folio);

            log.info("Added ${} to folio {} for minibar", totalAmount, folio.getId());
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MinibarConsumptionResponse> getConsumptionHistory(Long reservationId) {
        // Find reservation room
        ReservationRoomEntity reservationRoom = reservationRoomRepository
                .findByReservationEntityId(reservationId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.RESERVATION_ROOM_NOT_FOUND.code() + ": Reservation room not found"));

        // Get folio
        FolioEntity folio = folioRepository
                .findByReservationRoomEntityId(reservationRoom.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.FOLIO_NOT_FOUND.code() + ": Folio not found"));

        // Get minibar items from folio
        List<FolioItemEntity> minibarItems = folioItemRepository
                .findByFolioEntityIdAndType(folio.getId(), FolioItemType.MINIBAR_CHARGE);

        return minibarItems.stream()
                .map(item -> MinibarConsumptionResponse.builder()
                        .id(item.getId())
                        .roomNumber(reservationRoom.getRoomEntity().getRoomNumber())
                        .quantity(item.getQuantity())
                        .total(item.getTotalPrice().doubleValue())
                        .status(item.getStatus().name())
                        .createdAt(Timestamp.from(Instant.now())) // Add createdAt to entity if needed
                        .build())
                .collect(Collectors.toList());
    }

    private boolean isMinibarItem(AssetEntity asset) {
        // Check if asset category is minibar-related
        String categoryName = asset.getCategoryEntity().getName().toLowerCase();
        return categoryName.contains("minibar") ||
                categoryName.contains("beverage") ||
                categoryName.contains("snack");
    }

    private MinibarItemResponse convertToItemResponse(RoomAssetEntity asset) {
        return MinibarItemResponse.builder()
                .id(asset.getId())
                .assetName(asset.getAssetEntity().getName())
                .categoryName(asset.getAssetEntity().getCategoryEntity().getName())
                .currentQuantity(asset.getQuantity())
                .price(asset.getAssetEntity().getPrice())
                .status(asset.getStatus().name())
                .build();
    }
}