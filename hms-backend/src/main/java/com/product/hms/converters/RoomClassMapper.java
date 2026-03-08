package com.product.hms.converters;

import com.product.hms.dto.response.RoomClassResponse;
import com.product.hms.entity.RoomClassEntity;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for room class mappings.
 */
@Mapper(componentModel = "spring")
public interface RoomClassMapper {

    RoomClassResponse toResponse(RoomClassEntity entity);
}

