package com.product.hms.service;

import com.product.hms.dto.response.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for customer operations
 */
public interface CustomerService {

    /**
     * Tìm kiếm khách hàng theo số chứng minh nhân dân (identity card number).
     *
     * @param identityCard Số chứng minh nhân dân của khách hàng
     * @return Thông tin khách hàng nếu tìm thấy, null nếu không tìm thấy
     */
    CustomerResponse findCustomerByIdentityCard(String identityCard);

    // Pagination & filtering for customers
    Page<CustomerResponse> getCustomersWithPagination(String email, Boolean isActive, Pageable pageable);
}
