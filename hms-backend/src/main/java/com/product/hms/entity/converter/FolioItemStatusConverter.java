package com.product.hms.entity.converter;

import com.product.hms.entity.converter.base.BaseEnumStringConverter;
import com.product.hms.enums.FolioItemStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FolioItemStatusConverter extends BaseEnumStringConverter<FolioItemStatus> {
    public FolioItemStatusConverter() {
        super(FolioItemStatus.values());
    }
}

