package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;

public enum FolioStatus implements DbValueEnum {
    OPEN("OPEN"),
    LOCKED("LOCKED"),
    SETTLED("SETTLED");

    private final String dbValue;

    FolioStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }
}


