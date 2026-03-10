package com.product.hms.service.impl.reservation;

import com.product.hms.constants.Reservation;
import com.product.hms.dto.request.ReservationRequest;
import com.product.hms.entity.RoomClassEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public final class ReservationPricingSupport {

    private ReservationPricingSupport() {
    }

    public static BigDecimal calculateDepositForRequest(
            ReservationRequest request,
            Map<Long, RoomClassEntity> roomClassById
    ) {
        BigDecimal totalRoomCost = calculateTotalRoomCost(request, roomClassById);
        return totalRoomCost.multiply(Reservation.DEPOSIT_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalRoomCost(
            ReservationRequest request,
            Map<Long, RoomClassEntity> roomClassById
    ) {
        return request.roomClassQuantities().stream()
                .map(roomClassQuantityRequest -> {
                    RoomClassEntity roomClass = roomClassById.get(roomClassQuantityRequest.roomClassId());
                    Integer numberOfPeople = roomClassQuantityRequest.numberOfPeople();
                    return calculateRoomCostForAllocation(roomClass, numberOfPeople);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateRoomCostForAllocation(RoomClassEntity roomClass, Integer numberOfPeople) {
        BigDecimal baseCost = roomClass.getBasePrice();

        if (numberOfPeople > roomClass.getStandardCapacity()) {
            int extraPeople = numberOfPeople - roomClass.getStandardCapacity();
            BigDecimal extraFee = roomClass.getExtraPersonFee().multiply(BigDecimal.valueOf(extraPeople));
            baseCost = baseCost.add(extraFee);
        }
        return baseCost;
    }
}

