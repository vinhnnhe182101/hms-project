package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;

public enum FolioItemStatus implements DbValueEnum {
    UNPAID("UNPAID"),
    PAID("PAID"),
    VOID("VOID");

    private final String dbValue;

    FolioItemStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }
}
