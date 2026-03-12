package com.product.hms.api;

import com.product.hms.dto.request.ServiceSearchFilter;
import com.product.hms.dto.response.ServiceResponse;
import com.product.hms.service.ServiceSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceApi {
    private final ServiceSearchService serviceSearchService;

    @GetMapping
    public ResponseEntity<Page<ServiceResponse>> searchServices(
            ServiceSearchFilter filter,
            Pageable pageable
    ) {
        Page<ServiceResponse> result = serviceSearchService.search(filter, pageable);
        return ResponseEntity.ok(result);
    }
}

