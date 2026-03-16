// utils/SecurityUtil.java
package com.product.hms.utils;

import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.StaffEntity;
import com.product.hms.exception.ErrorCode;
import com.product.hms.exception.ResourceNotFoundException;
import com.product.hms.repository.CustomerRepository;
import com.product.hms.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;

    public StaffEntity getCurrentStaff() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return staffRepository.findByUserEntityEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.STAFF_NOT_FOUND.code() + ": Không tìm thấy nhân viên với email: " + email
                ));
    }

    public Long getCurrentStaffId() {
        return getCurrentStaff().getId();
    }

    public CustomerEntity getCurrentCustomer() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return customerRepository.findByUserEntityEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.CUSTOMER_NOT_FOUND.code() + ": Không tìm thấy khách hàng với email: " + email
                ));
    }

    public Long getCurrentCustomerId() {
        return getCurrentCustomer().getId();
    }
}