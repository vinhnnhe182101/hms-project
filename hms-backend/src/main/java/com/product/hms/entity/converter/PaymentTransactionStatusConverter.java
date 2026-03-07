package com.product.hms.entity.converter;

import com.product.hms.entity.converter.base.BaseEnumStringConverter;
import com.product.hms.enums.PaymentTransactionStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentTransactionStatusConverter extends BaseEnumStringConverter<PaymentTransactionStatus> {
    public PaymentTransactionStatusConverter() {
        super(PaymentTransactionStatus.values());
    }
}


