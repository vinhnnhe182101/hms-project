package com.product.hms.enums;

import com.product.hms.entity.converter.base.core.DbValueEnum;

public enum ServiceCategory implements DbValueEnum {
    MINIBAR, LAUNDRY, FOOD_BEVERAGE, SPA, TRANSPORT, OTHER;

    @Override
    public String getDbValue() {
        return "";
    }
}