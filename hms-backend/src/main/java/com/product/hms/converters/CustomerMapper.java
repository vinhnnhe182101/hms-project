package com.product.hms.converters;

import com.product.hms.dto.request.CustomerRequest;
import com.product.hms.dto.response.CustomerResponse;
import com.product.hms.entity.CustomerEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for customer mappings.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toResponse(CustomerEntity entity);

    // Create CustomerEntity from request payload for walk-in/new customers.
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "identityCard", source = "identityCard")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", source = "email")
    CustomerEntity toEntity(CustomerRequest request);
}
