package com.product.hms.service;

import com.product.hms.dto.response.CustomerResponse;

/**
 * Service interface for customer operations
 */
public interface CustomerService {

    /**
     * Find customer by identity card number
     *
     * @param identityCard the identity card number
     * @return CustomerResponse containing customer information
     */
    CustomerResponse findCustomerByIdentityCard(String identityCard);
}

