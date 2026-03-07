package com.product.hms.entity.converter;

import com.product.hms.entity.converter.base.BaseEnumStringConverter;
import com.product.hms.enums.PaymentTransactionType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentTransactionTypeConverter extends BaseEnumStringConverter<PaymentTransactionType> {
    public PaymentTransactionTypeConverter() {
        super(PaymentTransactionType.values());
    }
}


