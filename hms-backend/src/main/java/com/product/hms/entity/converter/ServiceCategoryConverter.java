package com.product.hms.entity.converter;

import com.product.hms.entity.converter.base.BaseEnumStringConverter;
import com.product.hms.enums.ServiceCategory;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ServiceCategoryConverter extends BaseEnumStringConverter<ServiceCategory> {
    public ServiceCategoryConverter() {
        super(ServiceCategory.values());
    }
}
