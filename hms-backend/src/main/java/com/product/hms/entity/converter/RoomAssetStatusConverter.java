package com.product.hms.entity.converter;

import com.product.hms.entity.converter.base.BaseEnumStringConverter;
import com.product.hms.enums.RoomAssetStatus;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoomAssetStatusConverter extends BaseEnumStringConverter<RoomAssetStatus> {
    public RoomAssetStatusConverter() {
        super(RoomAssetStatus.values());
    }
}
