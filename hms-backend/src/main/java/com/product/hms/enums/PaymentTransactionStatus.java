package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;

public enum PaymentTransactionStatus implements DbValueEnum {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    CANCELLED("CANCELLED");

    private final String dbValue;

    PaymentTransactionStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }
}


