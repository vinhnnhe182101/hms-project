package com.product.hms.dto.response;

import java.math.BigDecimal;

/**
 * Response DTO for folio item details
 *
 * @param id          Folio item ID
 * @param type        Type of charge (ROOM_CHARGE, SERVICE_CHARGE, EARLY_CHECKIN_FEE, etc.)
 * @param description Description of the charge
 * @param quantity    Quantity
 * @param totalPrice  Total price
 * @param status      Payment status (PAID, UNPAID, VOID)
 */
public record FolioItemResponse(
        Long id,
        String type,
        String description,
        Integer quantity,
        BigDecimal totalPrice,
        String status
) {
}

