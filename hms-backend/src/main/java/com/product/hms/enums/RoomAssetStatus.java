package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;
import lombok.Getter;

@Getter
public enum RoomAssetStatus implements DbValueEnum {
    GOOD("Good"),
    DAMAGED("Damaged");

    private final String dbValue;

    RoomAssetStatus(String dbValue) {
        this.dbValue = dbValue;
    }
}
