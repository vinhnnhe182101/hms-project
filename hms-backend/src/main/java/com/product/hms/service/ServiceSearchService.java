package com.product.hms.service;

import com.product.hms.dto.request.ServiceSearchFilter;
import com.product.hms.dto.response.ServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceSearchService {
    Page<ServiceResponse> search(ServiceSearchFilter filter, Pageable pageable);
}

