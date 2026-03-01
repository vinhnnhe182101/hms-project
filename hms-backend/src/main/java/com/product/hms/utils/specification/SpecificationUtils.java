package com.product.hms.utils.specification;

import com.product.hms.utils.specification.search.SearchCriteria;
import com.product.hms.utils.specification.sort.SortCriteria;
import jakarta.persistence.criteria.JoinType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("prototype")
public class SpecificationUtils<T> {
    private ObjectProvider<GenericSpecification<T>> genericSpecificationObjectProvider;
    private Map<SearchCriteria, LogicalOperator> searchCriteriaLogicalOperatorMap;
    private Map<SortCriteria, LogicalOperator> sortCriteriaLogicalOperatorMap;

    @Autowired
    public void setGenericSpecificationObjectProvider(ObjectProvider<GenericSpecification<T>> genericSpecificationObjectProvider) {
        this.genericSpecificationObjectProvider = genericSpecificationObjectProvider;
    }

    public SpecificationUtils<T> addSearchCriteria(SearchCriteria searchCriteria, LogicalOperator logicalOperator) {
        searchCriteriaLogicalOperatorMap.put(searchCriteria, logicalOperator);
        return this;
    }

    public SpecificationUtils<T> addSortCriteria(SortCriteria sortCriteria, LogicalOperator logicalOperator) {
        sortCriteriaLogicalOperatorMap.put(sortCriteria, logicalOperator);
        return this;
    }

    public Specification<T> getSearchSpecification() {
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSearchCriteriaLogicalOperatorMap(this.searchCriteriaLogicalOperatorMap);
        genericSpecification.setSortCriteriaLogicalOperatorMap(new HashMap<>());
        return genericSpecification;
    }

    public Specification<T> getSortSpecification() {
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSearchCriteriaLogicalOperatorMap(new HashMap<>());
        genericSpecification.setSortCriteriaLogicalOperatorMap(this.sortCriteriaLogicalOperatorMap);
        return genericSpecification;
    }

    public Specification<T> getSpecification() {
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSearchCriteriaLogicalOperatorMap(this.searchCriteriaLogicalOperatorMap);
        genericSpecification.setSortCriteriaLogicalOperatorMap(this.sortCriteriaLogicalOperatorMap);
        return genericSpecification;
    }

    public SpecificationUtils<T> reset() {
        this.searchCriteriaLogicalOperatorMap = new HashMap<>();
        this.sortCriteriaLogicalOperatorMap = new HashMap<>();
        return this;
    }

    public Specification<T> getSearchSpecification(SearchCriteria searchCriteria) {
        if (searchCriteria == null || searchCriteria.getFieldName() == null || searchCriteria.getComparisonOperator() == null || searchCriteria.getComparedValue() == null) {
            return null;
        }
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        this.searchCriteriaLogicalOperatorMap = Map.of(
                searchCriteria,
                LogicalOperator.AND
        );
        genericSpecification.setSearchCriteriaLogicalOperatorMap(this.searchCriteriaLogicalOperatorMap);
        genericSpecification.setSortCriteriaLogicalOperatorMap(new HashMap<>());
        return genericSpecification;
    }

    public Specification<T> getSortSpecification(SortCriteria sortCriteria) {
        if (sortCriteria == null || sortCriteria.getFieldName() == null || sortCriteria.getSortDirection() == null) {
            return null;
        }
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        this.sortCriteriaLogicalOperatorMap = Map.of(
                sortCriteria,
                LogicalOperator.AND
        );
        genericSpecification.setSearchCriteriaLogicalOperatorMap(new HashMap<>());
        genericSpecification.setSortCriteriaLogicalOperatorMap(this.sortCriteriaLogicalOperatorMap);
        return genericSpecification;
    }

    public Specification<T> getSearchSpecification(String fieldName, SearchCriteria.ComparisonOperator comparisonOperator, Object comparedValue, JoinType joinType) {
        if (fieldName == null || comparisonOperator == null || comparedValue == null) {
            return null;
        }
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSortCriteriaLogicalOperatorMap(
                Map.of(
                        SortCriteria.builder()
                                .fieldName(fieldName)
                                .sortDirection(SortCriteria.SortDirection.ASC)
                                .joinType(joinType)
                                .build(),
                        LogicalOperator.AND
                )
        );
        genericSpecification.setSearchCriteriaLogicalOperatorMap(this.searchCriteriaLogicalOperatorMap);
        genericSpecification.setSortCriteriaLogicalOperatorMap(new HashMap<>());
        return genericSpecification;
    }

    public Specification<T> getSortSpecification(String fieldName, SortCriteria.AggregationFunction aggregationFunction, SortCriteria.SortDirection sortDirection, JoinType joinType) {
        if (fieldName == null || aggregationFunction == null || sortDirection == null) {
            return null;
        }
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSearchCriteriaLogicalOperatorMap(new HashMap<>());
        genericSpecification.setSortCriteriaLogicalOperatorMap(
                Map.of(
                        SortCriteria.builder()
                                .fieldName(fieldName)
                                .aggregationFunction(aggregationFunction)
                                .sortDirection(sortDirection)
                                .joinType(joinType)
                                .build(),
                        LogicalOperator.AND
                )
        );
        return genericSpecification;
    }

    public Specification<T> getSearchSpecifications(SearchCriteria... searchCriterias) {
        return getSearchSpecifications(Arrays.asList(searchCriterias));
    }

    public Specification<T> getSortSpecifications(SortCriteria... sortCriterias) {
        return getSortSpecifications(Arrays.asList(sortCriterias));
    }

    public Specification<T> getSearchSpecifications(List<SearchCriteria> searchCriterias) {
        this.searchCriteriaLogicalOperatorMap = new LinkedHashMap<>();
        for (SearchCriteria searchCriteria : searchCriterias) {
            this.searchCriteriaLogicalOperatorMap.put(searchCriteria, LogicalOperator.AND);
        }
        return getSearchSpecification();
    }

    public Specification<T> getSortSpecifications(List<SortCriteria> sortCriterias) {
        this.sortCriteriaLogicalOperatorMap = new LinkedHashMap<>();
        for (SortCriteria sortCriteria : sortCriterias) {
            this.sortCriteriaLogicalOperatorMap.put(sortCriteria, LogicalOperator.AND);
        }
        return getSortSpecification();
    }

    public Specification<T> getSpecifications(List<SearchCriteria> searchCriterias, List<SortCriteria> sortCriterias) {
        for (SearchCriteria searchCriteria : searchCriterias) {
            this.searchCriteriaLogicalOperatorMap.put(searchCriteria, LogicalOperator.AND);
        }
        for (SortCriteria sortCriteria : sortCriterias) {
            this.sortCriteriaLogicalOperatorMap.put(sortCriteria, LogicalOperator.AND);
        }
        return getSpecification();
    }

    @Getter
    @AllArgsConstructor
    public enum LogicalOperator {
        AND("Và"),
        OR("Hoặc");

        private final String operator;
    }
}
