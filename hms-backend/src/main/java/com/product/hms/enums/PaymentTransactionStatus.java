package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;
import lombok.Getter;

@Getter
public enum PaymentTransactionStatus implements DbValueEnum {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    CANCELLED("CANCELLED");

    private final String dbValue;

    PaymentTransactionStatus(String dbValue) {
        this.dbValue = dbValue;
    }
}
