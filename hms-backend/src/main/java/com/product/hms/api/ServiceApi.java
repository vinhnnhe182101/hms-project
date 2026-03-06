package com.product.hms.api;

import com.product.hms.dto.response.ServiceResponse;
import com.product.hms.enums.ServiceCategory;
import com.product.hms.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceApi {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return ResponseEntity.ok(buildPageResponse(serviceService.getAllServices(pageable)));
    }


    @GetMapping("/by-category")
    public ResponseEntity<Map<String, Object>> getServicesByCategory(
            @RequestParam ServiceCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return ResponseEntity.ok(buildPageResponse(serviceService.getServicesByCategory(category, pageable)));
    }

    private Map<String, Object> buildPageResponse(Page<ServiceResponse> resultPage) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", resultPage.getContent());
        response.put("currentPage", resultPage.getNumber());
        response.put("totalItems", resultPage.getTotalElements());
        response.put("totalPages", resultPage.getTotalPages());
        response.put("pageSize", resultPage.getSize());
        response.put("isLast", resultPage.isLast());
        return response;
    }
}
