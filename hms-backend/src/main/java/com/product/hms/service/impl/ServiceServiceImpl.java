package com.product.hms.service.impl;

import com.product.hms.dto.request.CreateServiceRequest;
import com.product.hms.dto.response.ServiceResponse;
import com.product.hms.entity.ServiceEntity;
import com.product.hms.enums.ServiceCategory;
import com.product.hms.exception.BusinessException;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.ServiceRepository;
import com.product.hms.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Override
    @Transactional
    public ServiceResponse createService(CreateServiceRequest request) {
        ServiceEntity entity = new ServiceEntity();
        entity.setName(request.name().trim());
        entity.setServiceCategory(request.serviceCategory());
        entity.setPrice(request.price());
        entity.setIsActive(true);

        ServiceEntity saved = serviceRepository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ServiceResponse updateService(Long id, CreateServiceRequest request) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.SERVICE_NOT_FOUND,
                        "Service not found with id " + id
                ));

        entity.setName(request.name().trim());
        entity.setServiceCategory(request.serviceCategory());
        entity.setPrice(request.price());

        ServiceEntity updated = serviceRepository.save(entity);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.SERVICE_NOT_FOUND,
                        "Service not found with id " + id
                ));

        try {
            serviceRepository.delete(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(
                    ErrorCode.SERVICE_BOOKING_NOT_ALLOWED,
                    "Cannot delete service because it is being used in service bookings"
            );
        }
    }

    private ServiceResponse mapToResponse(ServiceEntity entity) {
        return ServiceResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .serviceCategory(entity.getServiceCategory())
                .isActive(entity.getIsActive())
                .build();
    }
}
