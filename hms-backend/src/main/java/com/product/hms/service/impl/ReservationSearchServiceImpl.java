package com.product.hms.service.impl;

import com.product.hms.converters.CustomerMapper;
import com.product.hms.dto.request.ReservationSearchFilter;
import com.product.hms.dto.response.CustomerResponse;
import com.product.hms.dto.response.ReservationResponse;
import com.product.hms.dto.response.RoomClassQuantityResponse;
import com.product.hms.entity.ReservationEntity;
import com.product.hms.repository.ReservationRepository;
import com.product.hms.repository.ReservationRoomRepository;
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
import static com.product.hms.utils.specification.sort.SortCriteria.SortDirection.DESC;

@Service
@RequiredArgsConstructor
public class ReservationSearchServiceImpl implements ReservationSearchService {
    private final ReservationRepository reservationRepository;
    private final SpecificationUtils<ReservationEntity> specificationUtils;
    private final ReservationRoomRepository reservationRoomRepository;
    private final CustomerMapper customerMapper;

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
        CustomerResponse customer = customerMapper.toResponse(entity.getCustomerEntity());
        List<RoomClassQuantityResponse> allocations = reservationRoomRepository
            .findByReservationEntity(entity)
            .stream()
            .map(allocation -> new RoomClassQuantityResponse(
                allocation.getId(),
                allocation.getRoomClassEntity() != null ? allocation.getRoomClassEntity().getId() : null,
                allocation.getNumberOfPeople()
            ))
            .toList();
        return new ReservationResponse(
            entity.getId(),
            entity.getCode(),
            customer,
            allocations,
            entity.getExpectedCheckIn(),
            entity.getExpectedCheckOut(),
            entity.getStatus() != null ? entity.getStatus().name() : null,
            entity.getNumberOfMembers(),
            entity.getNote(),
            entity.getCreatedAt()
        );
    }
}
