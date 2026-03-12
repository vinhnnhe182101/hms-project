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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/home/services")
@RequiredArgsConstructor

public class HomeServiceApi {

    private final ServiceService serviceService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getServices(
            @RequestParam(required = false) ServiceCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<ServiceResponse> resultPage;
        
        if (category != null) {
            resultPage = serviceService.getServicesByCategory(category, pageable);
        } else {
            resultPage = serviceService.getAllServices(pageable);
        }
        
        return ResponseEntity.ok(buildPageResponse(resultPage));
    }

    @GetMapping("/categories")
    public ResponseEntity<ServiceCategory[]> getCategories() {
        return ResponseEntity.ok(ServiceCategory.values());
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
