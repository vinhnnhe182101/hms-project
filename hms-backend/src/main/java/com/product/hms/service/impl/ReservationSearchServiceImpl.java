package com.product.hms.service.impl;

import com.product.hms.dto.request.ReservationSearchFilter;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.repository.ReservationRepository;
import com.product.hms.service.ReservationSearchService;
import com.product.hms.utils.specification.SpecificationUtils;
import com.product.hms.utils.specification.search.SearchCriteria;
import com.product.hms.utils.specification.sort.SortCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import static com.product.hms.utils.specification.search.SearchCriteria.ComparisonOperator.*;
import static com.product.hms.utils.specification.sort.SortCriteria.SortDirection.*;

@Service
@RequiredArgsConstructor
public class ReservationSearchServiceImpl implements ReservationSearchService {
    private final ReservationRepository reservationRepository;
    private final SpecificationUtils<ReservationEntity> specificationUtils;

    @Override
    public Page<ReservationResponse> search(ReservationSearchFilter filter, Pageable pageable) {
        List<SearchCriteria> searchCriteria = new ArrayList<>();
        searchCriteria.add(new SearchCriteria("guestName", LIKE, filter.guestName()));
        searchCriteria.add(new SearchCriteria("status", EQUALS, filter.status()));
        searchCriteria.add(new SearchCriteria("checkInDate", GREATER_THAN_OR_EQUAL_TO, filter.checkInDateFrom()));
        searchCriteria.add(new SearchCriteria("checkInDate", LESS_THAN_OR_EQUAL_TO, filter.checkInDateTo()));
        List<SortCriteria> sortCriteria = new ArrayList<>();
        if (pageable.getSort().isEmpty()) {
            sortCriteria.add(new SortCriteria("checkInDate", null, DESC, null));
        }
        var spec = specificationUtils.getSpecifications(searchCriteria, sortCriteria);
        return reservationRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private ReservationResponse toResponse(ReservationEntity entity) {
        // TODO: Map fields from entity to response
        return null;
    }
}

