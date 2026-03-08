package com.product.hms.service;

import com.product.hms.dto.response.ServiceResponse;
import com.product.hms.enums.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceService {


    Page<ServiceResponse> getAllServices(Pageable pageable);

    Page<ServiceResponse> getServicesByCategory(ServiceCategory category, Pageable pageable);
}