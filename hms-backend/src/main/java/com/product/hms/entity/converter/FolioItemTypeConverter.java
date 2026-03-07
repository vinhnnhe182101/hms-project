package com.product.hms.entity.converter;

import com.product.hms.entity.converter.base.BaseEnumStringConverter;
import com.product.hms.enums.FolioItemType;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FolioItemTypeConverter extends BaseEnumStringConverter<FolioItemType> {
    public FolioItemTypeConverter() {
        super(FolioItemType.values());
    }
}

