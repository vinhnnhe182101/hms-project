package com.product.hms.entity.converter;

import com.product.hms.entity.converter.base.BaseEnumStringConverter;
import com.product.hms.enums.PaymentMethod;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter extends BaseEnumStringConverter<PaymentMethod> {
    public PaymentMethodConverter() {
        super(PaymentMethod.values());
    }
}


