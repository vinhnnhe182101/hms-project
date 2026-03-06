package com.product.hms.service;

import com.product.hms.dto.response.ServiceResponse;
import com.product.hms.enums.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceService {

    /**
     * Lấy toàn bộ danh sách dịch vụ có phân trang.
     */
    Page<ServiceResponse> getAllServices(Pageable pageable);

    /**
     * Lấy danh sách dịch vụ theo danh mục Enum, có phân trang.
     */
    Page<ServiceResponse> getServicesByCategory(ServiceCategory category, Pageable pageable);
}