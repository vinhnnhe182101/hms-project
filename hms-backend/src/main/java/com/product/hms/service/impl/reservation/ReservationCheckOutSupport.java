package com.product.hms.service.impl.reservation;

import com.product.hms.entity.ReservationRoomEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Support class for late check-out fee calculation logic (BR-09)
 */
public final class ReservationCheckOutSupport {

    private ReservationCheckOutSupport() {
    }

    /**
     * Calculate late check-out fee based on actual check-out time vs expected.
     * BR-09: Standard check-out = 12:00
     * - 12:00 - 15:00: 30% of room rate
     * - 15:00 - 18:00: 50% of room rate
     * - After 18:00: 100% of room rate (extra night)
     */
    public static BigDecimal calculateLateCheckOutFee(
            ReservationRoomEntity allocation,
            Timestamp expectedCheckOut
    ) {
        if (allocation.getActualCheckOut() == null || expectedCheckOut == null) {
            return BigDecimal.ZERO;
        }

        Instant actualCheckOut = allocation.getActualCheckOut();
        Instant expected = expectedCheckOut.toInstant();

        // If checked out on time or earlier, no fee
        if (!actualCheckOut.isAfter(expected)) {
            return BigDecimal.ZERO;
        }

        BigDecimal rate = determineLateCheckOutRate(actualCheckOut, expectedCheckOut);
        if (rate.signum() <= 0 || allocation.getPriceAtBooking() == null) {
            return BigDecimal.ZERO;
        }

        return allocation.getPriceAtBooking().multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Determine late check-out rate based on time.
     * Standard check-out time: 12:00
     */
    private static BigDecimal determineLateCheckOutRate(Instant actualCheckOut, Timestamp expectedCheckOut) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate actualDate = actualCheckOut.atZone(zoneId).toLocalDate();
        LocalDate expectedDate = expectedCheckOut.toInstant().atZone(zoneId).toLocalDate();

        // Check-out after expected date is treated as extra night(s)
        if (actualDate.isAfter(expectedDate)) {
            return BigDecimal.ONE;
        }

        LocalTime checkOutTime = actualCheckOut.atZone(zoneId).toLocalTime();
        LocalTime standardCheckOut = LocalTime.of(12, 0);

        // On time or early
        if (!checkOutTime.isAfter(standardCheckOut)) {
            return BigDecimal.ZERO;
        }

        // 12:00 - 15:00: 30%
        if (checkOutTime.isBefore(LocalTime.of(15, 0))) {
            return new BigDecimal("0.30");
        }

        // 15:00 - 18:00: 50%
        if (checkOutTime.isBefore(LocalTime.of(18, 0))) {
            return new BigDecimal("0.50");
        }

        // After 18:00: 100% (extra night)
        return BigDecimal.ONE;
    }
}

