package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;
import lombok.Getter;

@Getter
public enum FolioStatus implements DbValueEnum {
    OPEN("OPEN"),
    LOCKED("LOCKED"),
    CLOSED("CLOSED"),
    SETTLED("SETTLED");

    private final String dbValue;

    FolioStatus(String dbValue) {
        this.dbValue = dbValue;
    }
}


