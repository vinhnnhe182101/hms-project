package com.product.hms.service.impl;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.response.CustomerResponse;
import com.product.hms.entity.CustomerEntity;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.NotFoundException;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of CustomerService
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse findCustomerByIdentityCard(String identityCard) {
        CustomerEntity customer = customerRepository.findByIdentityCard(identityCard)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.CUSTOMER_NOT_FOUND,
                        "Customer not found with identity card: " + identityCard
                ));

        return customerMapper.toResponse(customer);
    }
}
