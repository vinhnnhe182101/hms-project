package com.product.hms.api;

import com.product.hms.dto.request.CreateServiceRequest;
import com.product.hms.dto.request.ServiceSearchFilter;
import com.product.hms.dto.response.ServiceResponse;
import com.product.hms.service.ServiceService;
import com.product.hms.service.ServiceSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceApi {
    private final ServiceSearchService serviceSearchService;
    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<Page<ServiceResponse>> searchServices(
            ServiceSearchFilter filter,
            Pageable pageable
    ) {
        Page<ServiceResponse> result = serviceSearchService.search(filter, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody CreateServiceRequest request) {
        ServiceResponse response = serviceService.createService(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody CreateServiceRequest request
    ) {
        ServiceResponse response = serviceService.updateService(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}

