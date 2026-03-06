package com.product.hms.service.impl;

import com.product.hms.dto.response.ServiceResponse;
import com.product.hms.entity.ServiceEntity;
import com.product.hms.enums.ServiceCategory;
import com.product.hms.repository.ServiceRepository;
import com.product.hms.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceResponse> getAllServices(Pageable pageable) {
        return serviceRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceResponse> getServicesByCategory(ServiceCategory category, Pageable pageable) {
        return serviceRepository.findByServiceCategory(category, pageable)
                .map(this::mapToResponse);
    }

    private ServiceResponse mapToResponse(ServiceEntity entity) {
        return ServiceResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .serviceCategory(entity.getServiceCategory())
                .build();
    }
}
