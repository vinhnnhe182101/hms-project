package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;
import lombok.Getter;

@Getter
public enum PaymentMethod implements DbValueEnum {
    CASH("CASH"),
    CARD("CARD"),
    BANK_TRANSFER("BANK_TRANSFER"),
    QR("QR"),
    VNPAY("VNPAY");

    private final String dbValue;

    PaymentMethod(String dbValue) {
        this.dbValue = dbValue;
    }
}
