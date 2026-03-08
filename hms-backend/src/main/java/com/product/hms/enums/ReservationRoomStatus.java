package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;
import lombok.Getter;

@Getter
public enum ReservationRoomStatus implements DbValueEnum {
    PENDING("PENDING"),
    ASSIGNED("ASSIGNED"),
    CHECKED_IN("CHECKED_IN"),
    CHECKED_OUT("CHECKED_OUT"),
    CANCELLED("CANCELLED");

    private final String dbValue;

    ReservationRoomStatus(String dbValue) {
        this.dbValue = dbValue;
    }
}

