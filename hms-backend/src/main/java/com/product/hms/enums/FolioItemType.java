package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;

public enum FolioItemType implements DbValueEnum {
    ROOM_CHARGE("ROOM_CHARGE"),
    SERVICE_CHARGE("SERVICE_CHARGE"),
    EARLY_CHECKIN_FEE("EARLY_CHECKIN_FEE"),
    LATE_CHECKOUT_FEE("LATE_CHECKOUT_FEE"),
    DAMAGE_PENALTY("DAMAGE_PENALTY"),
    ADJUSTMENT("ADJUSTMENT"),
    DISCOUNT("DISCOUNT"),
    REFUND("REFUND");

    private final String dbValue;

    FolioItemType(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }
}
