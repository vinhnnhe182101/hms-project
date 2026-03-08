package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;
import lombok.Getter;

@Getter
public enum FolioItemStatus implements DbValueEnum {
    UNPAID("UNPAID"),
    PAID("PAID"),
    VOID("VOID");

    private final String dbValue;

    FolioItemStatus(String dbValue) {
        this.dbValue = dbValue;
    }
}
