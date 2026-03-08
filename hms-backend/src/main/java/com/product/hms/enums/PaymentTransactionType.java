package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;

public enum PaymentTransactionType implements DbValueEnum {
    DEPOSIT("DEPOSIT"),
    PAYMENT("PAYMENT"),
    REFUND("REFUND"),
    ADJUSTMENT("ADJUSTMENT");

    private final String dbValue;

    PaymentTransactionType(String dbValue) {
        this.dbValue = dbValue;
    }

    @Override
    public String getDbValue() {
        return dbValue;
    }
}


