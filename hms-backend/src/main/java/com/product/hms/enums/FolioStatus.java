package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;

public enum FolioStatus implements DbValueEnum {
    OPEN(),
    LOCKED(),
    SETTLED(), CLOSED();

    private String dbValue = "";

    FolioStatus() {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }
}
