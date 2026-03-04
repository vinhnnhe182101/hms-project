package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;

public enum ServiceCategory implements DbValueEnum {
    SPA("Spa"),
    MINIBAR("Minibar"),
    F_AND_B("F&B");

    private final String dbValue;

    ServiceCategory(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }
}
