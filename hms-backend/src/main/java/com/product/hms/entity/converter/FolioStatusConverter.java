package com.product.hms.entity.converter;

import com.product.hms.entity.converter.base.BaseEnumStringConverter;
import com.product.hms.enums.FolioStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FolioStatusConverter extends BaseEnumStringConverter<FolioStatus> {
    public FolioStatusConverter() {
        super(FolioStatus.values());
    }
}


