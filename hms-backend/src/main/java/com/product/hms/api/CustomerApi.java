package com.product.hms.api;

import com.product.hms.dto.response.CustomerResponse;
import com.product.hms.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API controller for customer operations
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerApi {

    private final CustomerService customerService;

    /**
     * Find customer by identity card number
     *
     * @param identityCard the identity card number
     * @return ResponseEntity containing customer information
     */
    @GetMapping("/search")
    public ResponseEntity<CustomerResponse> findCustomerByIdentityCard(
            @RequestParam("identityCard") String identityCard) {

        CustomerResponse customer = customerService.findCustomerByIdentityCard(identityCard);
        return ResponseEntity.ok(customer);
    }
}
