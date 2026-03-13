package com.product.hms.service.impl;

import com.product.hms.dto.request.ServiceSearchFilter;
import com.product.hms.dto.response.ServiceResponse;
import com.product.hms.entity.ServiceEntity;
import com.product.hms.repository.ServiceRepository;
import com.product.hms.service.ServiceSearchService;
import com.product.hms.utils.specification.SpecificationUtils;
import com.product.hms.utils.specification.search.SearchCriteria;
import com.product.hms.utils.specification.sort.SortCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.product.hms.utils.specification.search.SearchCriteria.ComparisonOperator.CONTAINS;
import static com.product.hms.utils.specification.search.SearchCriteria.ComparisonOperator.EQUALS;
import static com.product.hms.utils.specification.sort.SortCriteria.SortDirection.ASC;

@Service
@RequiredArgsConstructor
public class ServiceSearchServiceImpl implements ServiceSearchService {
    private final ServiceRepository serviceRepository;
    private final SpecificationUtils<ServiceEntity> specificationUtils;

    @Override
    public Page<ServiceResponse> search(ServiceSearchFilter filter, Pageable pageable) {
        List<SearchCriteria> searchCriteria = new ArrayList<>();
        searchCriteria.add(new SearchCriteria("name", CONTAINS, filter.name()));
        searchCriteria.add(new SearchCriteria("serviceCategory", EQUALS, filter.category()));
        searchCriteria.add(new SearchCriteria("isActive", EQUALS, filter.status()));
        List<SortCriteria> sortCriteria = new ArrayList<>();
        if (pageable.getSort().isEmpty()) {
            sortCriteria.add(new SortCriteria("name", null, ASC, null));
        }
        var spec = specificationUtils.getSpecifications(searchCriteria, sortCriteria);
        return serviceRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private ServiceResponse toResponse(ServiceEntity entity) {
        return new ServiceResponse(
                entity.getId(),
                entity.getName(),
                entity.getServiceCategory(),
                entity.getPrice(),
                entity.getIsActive()
        );
    }
}
