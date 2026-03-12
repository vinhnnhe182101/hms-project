package com.product.hms.service.impl;

import com.product.hms.dto.request.RoomSearchFilter;
import com.product.hms.dto.response.RoomClassAvailableRoomsResponse;
import com.product.hms.entity.RoomEntity;
import com.product.hms.repository.RoomRepository;
import com.product.hms.service.RoomSearchService;
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
public class RoomSearchServiceImpl implements RoomSearchService {
    private final RoomRepository roomRepository;
    private final SpecificationUtils<RoomEntity> specificationUtils;

    @Override
    public Page<RoomClassAvailableRoomsResponse> search(RoomSearchFilter filter, Pageable pageable) {
        List<SearchCriteria> searchCriteria = new ArrayList<>();
        searchCriteria.add(new SearchCriteria("roomNumber", LIKE, filter.roomNumber()));
        searchCriteria.add(new SearchCriteria("roomClassEntity.id", EQUALS, filter.roomClassId()));
        searchCriteria.add(new SearchCriteria("status", EQUALS, filter.status()));
        List<SortCriteria> sortCriteria = new ArrayList<>();
        if (pageable.getSort().isEmpty()) {
            sortCriteria.add(new SortCriteria("roomNumber", null, ASC, null));
        }
        var spec = specificationUtils.getSpecifications(searchCriteria, sortCriteria);
        return roomRepository.findAll(spec, pageable).map(this::toResponse);
    }

    private RoomClassAvailableRoomsResponse toResponse(RoomEntity entity) {
        // TODO: Map fields from entity to response
        return null;
    }
}

