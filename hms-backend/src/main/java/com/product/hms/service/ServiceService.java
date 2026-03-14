package com.product.hms.service;

import com.product.hms.dto.request.CreateServiceRequest;
import com.product.hms.dto.response.ServiceResponse;
import com.product.hms.enums.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceService {


    Page<ServiceResponse> getAllServices(Pageable pageable);

    Page<ServiceResponse> getServicesByCategory(ServiceCategory category, Pageable pageable);

    ServiceResponse createService(CreateServiceRequest request);

    ServiceResponse updateService(Long id, CreateServiceRequest request);

    void deleteService(Long id);
}