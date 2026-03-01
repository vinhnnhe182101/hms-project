package com.product.hms.utils.specification;

import com.product.hms.utils.NumberTypeUtils;
import com.product.hms.utils.PathUtils;
import com.product.hms.utils.specification.exception.ResourceUnsupportedException;
import com.product.hms.utils.specification.search.SearchCriteria;
import com.product.hms.utils.specification.sort.SortCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope("prototype") // Use prototype scope to allow multiple instances with different sort criteria
@SuppressWarnings({"unchecked", "rawtypes"})
public class GenericSpecification<T> implements Specification<T> {

    private static final Map<SortCriteria.AggregationFunction, SortCriteriaOrderProvider> SORT_CRITERIA_ORDER_PROVIDER_MAP = Map.of(
            SortCriteria.AggregationFunction.NONE, (realPath, javaType, root, criteriaQuery, criteriaBuilder, sortCriteria, joinMap) -> {
                criteriaQuery.orderBy(
                        sortCriteria.getSortDirection().equals(SortCriteria.SortDirection.ASC)
                                ? criteriaBuilder.asc(realPath)
                                : criteriaBuilder.desc(realPath)
                );
            },
            SortCriteria.AggregationFunction.COUNT, (realPath, javaType, root, criteriaQuery, criteriaBuilder, sortCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                criteriaQuery.orderBy(
                        sortCriteria.getSortDirection().equals(SortCriteria.SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.count(realPath))
                                : criteriaBuilder.desc(criteriaBuilder.count(realPath))
                );
            },
            SortCriteria.AggregationFunction.SUM, (realPath, javaType, root, criteriaQuery, criteriaBuilder, sortCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                criteriaQuery.orderBy(
                        sortCriteria.getSortDirection().equals(SortCriteria.SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)))
                );
            },
            SortCriteria.AggregationFunction.AVG, (realPath, javaType, root, criteriaQuery, criteriaBuilder, sortCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                criteriaQuery.orderBy(
                        sortCriteria.getSortDirection().equals(SortCriteria.SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)))
                );
            },
            SortCriteria.AggregationFunction.MAX, (realPath, javaType, root, criteriaQuery, criteriaBuilder, sortCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                criteriaQuery.orderBy(
                        sortCriteria.getSortDirection().equals(SortCriteria.SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.max(PathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.max(PathUtils.getPath(realPath, javaType)))
                );
            },
            SortCriteria.AggregationFunction.MIN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, sortCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                criteriaQuery.orderBy(
                        sortCriteria.getSortDirection().equals(SortCriteria.SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.min(PathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.min(PathUtils.getPath(realPath, javaType)))
                );
            }
    );
    private Map<SortCriteria, SpecificationUtils.LogicalOperator> sortCriteriaLogicalOperatorMap = new HashMap<>();
    private Map<SearchCriteria, SpecificationUtils.LogicalOperator> searchCriteriaLogicalOperatorMap = new HashMap<>();
    private EntityManager entityManager;
    private List<HavingCondition> havingConditions = new LinkedList<>();
    private final Map<SearchCriteria.ComparisonOperator, SearchCriteriaPredicateProvider> SEARCH_CRITERIA_PREDICATE_PROVIDER_MAP = Map.ofEntries(
            // ----------- SO SÁNH GIÁ TRỊ ----------- //
            Map.entry(SearchCriteria.ComparisonOperator.EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.equal(realPath, searchCriteria.getComparedValue())
            ),
            Map.entry(SearchCriteria.ComparisonOperator.NOT_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.notEqual(realPath, searchCriteria.getComparedValue())
            ),
            Map.entry(SearchCriteria.ComparisonOperator.GREATER_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.greaterThan(
                            PathUtils.getPath(realPath, javaType),
                            (Comparable) searchCriteria.getComparedValue()
                    )
            ),
            Map.entry(SearchCriteria.ComparisonOperator.LESS_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.lessThan(
                            PathUtils.getPath(realPath, javaType),
                            (Comparable) searchCriteria.getComparedValue()
                    )
            ),
            Map.entry(SearchCriteria.ComparisonOperator.GREATER_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.greaterThanOrEqualTo(
                            PathUtils.getPath(realPath, javaType),
                            (Comparable) searchCriteria.getComparedValue()
                    )
            ),
            Map.entry(SearchCriteria.ComparisonOperator.LESS_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.lessThanOrEqualTo(
                            PathUtils.getPath(realPath, javaType),
                            (Comparable) searchCriteria.getComparedValue()
                    )
            ),

            // ----------- SO SÁNH CHUỖI ----------- //
            Map.entry(SearchCriteria.ComparisonOperator.LIKE, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.like(
                            PathUtils.getPath(realPath, javaType),
                            searchCriteria.getComparedValue().toString()
                    )
            ),
            Map.entry(SearchCriteria.ComparisonOperator.NOT_LIKE, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.notLike(
                            PathUtils.getPath(realPath, javaType),
                            searchCriteria.getComparedValue().toString()
                    )
            ),
            Map.entry(SearchCriteria.ComparisonOperator.CONTAINS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.like(
                            PathUtils.getPath(realPath, javaType),
                            "%" + searchCriteria.getComparedValue() + "%"
                    )
            ),
            Map.entry(SearchCriteria.ComparisonOperator.NOT_CONTAINS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.notLike(
                            PathUtils.getPath(realPath, javaType),
                            "%" + searchCriteria.getComparedValue() + "%"
                    )
            ),

            // ----------- TẬP HỢP ----------- //
            Map.entry(SearchCriteria.ComparisonOperator.IN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    realPath.in(searchCriteria.getComparedValue())
            ),
            Map.entry(SearchCriteria.ComparisonOperator.NOT_IN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.not(
                            realPath.in(searchCriteria.getComparedValue())
                    )
            ),

            // ----------- KIỂM TRA NULL ----------- //
            Map.entry(SearchCriteria.ComparisonOperator.IS_NULL, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.isNull(realPath)
            ),
            Map.entry(SearchCriteria.ComparisonOperator.IS_NOT_NULL, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) ->
                    criteriaBuilder.isNotNull(realPath)
            ),

            // ----------- KHOẢNG GIÁ TRỊ ----------- //
            Map.entry(SearchCriteria.ComparisonOperator.BETWEEN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                Object[] values = (Object[]) searchCriteria.getComparedValue();
                return criteriaBuilder.between(
                        PathUtils.getPath(realPath, javaType),
                        (Comparable) values[0],
                        (Comparable) values[1]
                );
            }),
            Map.entry(SearchCriteria.ComparisonOperator.NOT_BETWEEN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                Object[] values = (Object[]) searchCriteria.getComparedValue();
                return criteriaBuilder.not(
                        criteriaBuilder.between(
                                PathUtils.getPath(realPath, javaType),
                                (Comparable) values[0],
                                (Comparable) values[1]
                        )
                );
            }),

            // ----------- AVG ----------- //
            Map.entry(SearchCriteria.ComparisonOperator.AVG_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.equal(
                                criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.AVG_NOT_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.notEqual(
                                criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.AVG_GREATER_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.greaterThan(
                                criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.AVG_LESS_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.lessThan(
                                criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.AVG_GREATER_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.greaterThanOrEqualTo(
                                criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.AVG_LESS_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.lessThanOrEqualTo(
                                criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),

            // ----------- COUNT ----------- //
            Map.entry(SearchCriteria.ComparisonOperator.COUNT_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.equal(
                                criteriaBuilder.count(realPath),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.COUNT_NOT_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.notEqual(
                                criteriaBuilder.count(realPath),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.COUNT_GREATER_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.greaterThan(
                                criteriaBuilder.count(realPath),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.COUNT_LESS_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.lessThan(
                                criteriaBuilder.count(realPath),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.COUNT_GREATER_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.greaterThanOrEqualTo(
                                criteriaBuilder.count(realPath),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.COUNT_LESS_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.lessThanOrEqualTo(
                                criteriaBuilder.count(realPath),
                                value
                        )
                ).build());
                return null;
            }),

            // ----------- SUM ----------- //
            Map.entry(SearchCriteria.ComparisonOperator.SUM_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.equal(
                                criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.SUM_NOT_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.notEqual(
                                criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.SUM_GREATER_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.greaterThan(
                                criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.SUM_LESS_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.lessThan(
                                criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.SUM_GREATER_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.greaterThanOrEqualTo(
                                criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.SUM_LESS_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.lessThanOrEqualTo(
                                criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),

            // ----------- MAX ----------- //
            Map.entry(SearchCriteria.ComparisonOperator.MAX_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.equal(
                                criteriaBuilder.max(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.MAX_NOT_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.notEqual(
                                criteriaBuilder.max(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.MAX_GREATER_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.greaterThan(
                                criteriaBuilder.max(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.MAX_LESS_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.lessThan(
                                criteriaBuilder.max(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.MAX_GREATER_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.greaterThanOrEqualTo(
                                criteriaBuilder.max(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.MAX_LESS_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.lessThanOrEqualTo(
                                criteriaBuilder.max(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),

            // ----------- MIN ----------- //
            Map.entry(SearchCriteria.ComparisonOperator.MIN_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.equal(
                                criteriaBuilder.min(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.MIN_NOT_EQUALS, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.notEqual(
                                criteriaBuilder.min(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.MIN_GREATER_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.greaterThan(
                                criteriaBuilder.min(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.MIN_LESS_THAN, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.lessThan(
                                criteriaBuilder.min(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.MIN_GREATER_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.greaterThanOrEqualTo(
                                criteriaBuilder.min(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            }),
            Map.entry(SearchCriteria.ComparisonOperator.MIN_LESS_THAN_OR_EQUAL_TO, (realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap) -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(
                        criteriaBuilder.lessThanOrEqualTo(
                                criteriaBuilder.min(PathUtils.getPath(realPath, javaType)),
                                value
                        )
                ).build());
                return null;
            })
    );

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Predicate getPredicate(@NonNull Root<T> root, CriteriaQuery<?> criteriaQuery, @NonNull CriteriaBuilder criteriaBuilder, SearchCriteria searchCriteria, Map<String, Join<?, ?>> joinMap) {
        // Tạo đường dẫn truy cập thuộc tính
        PathUtils.join(root, searchCriteria.getFieldName(), searchCriteria.getJoinType(), joinMap);
        Path<Object> realPath = PathUtils.getRealPath(root, searchCriteria.getFieldName(), joinMap);
        Class<?> javaType = root.getJavaType();

        SearchCriteriaPredicateProvider searchCriteriaPredicateProvider = SEARCH_CRITERIA_PREDICATE_PROVIDER_MAP.get(searchCriteria.getComparisonOperator());
        if (searchCriteriaPredicateProvider != null) {
            return searchCriteriaPredicateProvider.apply(realPath, javaType, root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap);
        }
        throw new ResourceUnsupportedException("Unsupported search criteria: " + searchCriteria);
    }

    private Predicate getPredicate(@NonNull Root<T> root, CriteriaQuery<?> criteriaQuery, @NonNull CriteriaBuilder criteriaBuilder, SortCriteria sortCriteria, Map<String, Join<?, ?>> joinMap) {
        PathUtils.join(root, sortCriteria.getFieldName(), sortCriteria.getJoinType(), joinMap);
        Path<Object> realPath = PathUtils.getRealPath(root, sortCriteria.getFieldName(), joinMap);
        Class<?> javaType = root.getJavaType();

        SortCriteriaOrderProvider sortCriteriaOrderProvider = SORT_CRITERIA_ORDER_PROVIDER_MAP.get(sortCriteria.getAggregationFunction());
        if (sortCriteriaOrderProvider != null) {
            sortCriteriaOrderProvider.apply(realPath, javaType, root, criteriaQuery, criteriaBuilder, sortCriteria, joinMap);
        }

        return criteriaBuilder.conjunction();
    }

    private <C> Predicate mapToPredicate(@NonNull Root<T> root, CriteriaQuery<?> criteriaQuery, @NonNull CriteriaBuilder criteriaBuilder, Map<String, Join<?, ?>> joinMap, Map<C, SpecificationUtils.LogicalOperator> criteriaLogicalOperatorMap) {
        Predicate predicate = criteriaBuilder.conjunction();
        for (Map.Entry<C, SpecificationUtils.LogicalOperator> entry : criteriaLogicalOperatorMap.entrySet()) {
            C criteria = entry.getKey();
            SpecificationUtils.LogicalOperator logicalOperator = entry.getValue();
            if (criteria != null && logicalOperator != null) {
                if (criteria instanceof SortCriteria sortCriteria) {
                    if (sortCriteria.getFieldName() == null || sortCriteria.getSortDirection() == null || sortCriteria.getAggregationFunction() == null) {
                        continue; // Skip if sort criteria is incomplete
                    }
                    Predicate sortPredicate = getPredicate(root, criteriaQuery, criteriaBuilder, sortCriteria, joinMap);
                    if (sortPredicate != null) {
                        predicate = logicalOperator.equals(SpecificationUtils.LogicalOperator.AND) ? criteriaBuilder.and(predicate, sortPredicate) : criteriaBuilder.or(predicate, sortPredicate);
                    }
                } else if (criteria instanceof SearchCriteria searchCriteria) {
                    if (searchCriteria.getFieldName() == null || searchCriteria.getComparisonOperator() == null || searchCriteria.getComparedValue() == null) {
                        continue; // Skip if search criteria is incomplete
                    }
                    int havingPredicateCount = havingConditions.size();
                    Predicate searchPredicate = getPredicate(root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap);
                    if (searchPredicate != null) {
                        predicate = logicalOperator.equals(SpecificationUtils.LogicalOperator.AND) ? criteriaBuilder.and(predicate, searchPredicate) : criteriaBuilder.or(predicate, searchPredicate);
                    } else {
                        if (havingConditions.size() != havingPredicateCount) {
                            HavingCondition havingCondition = havingConditions.get(havingConditions.size() - 1);
                            havingCondition.logicalOperator = logicalOperator;
                            havingConditions.set(havingConditions.size() - 1, havingCondition);
                        }
                    }
                }
            }
        }
        return predicate;
    }

    private Predicate buildHavingPredicate() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Predicate havingPredicate = criteriaBuilder.conjunction();
        for (HavingCondition condition : havingConditions) {
            if (condition.getPredicate() != null) {
                if (condition.getLogicalOperator() == SpecificationUtils.LogicalOperator.AND) {
                    havingPredicate = criteriaBuilder.and(havingPredicate, condition.getPredicate());
                } else if (condition.getLogicalOperator() == SpecificationUtils.LogicalOperator.OR) {
                    havingPredicate = criteriaBuilder.or(havingPredicate, condition.getPredicate());
                } else {
                    throw new ResourceUnsupportedException("Unsupported logical operator in having condition: " + condition.getLogicalOperator());
                }
            }
        }
        return havingPredicate;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        Map<String, Join<?, ?>> joinMap = new HashMap<>();
        havingConditions = new LinkedList<>();
        Predicate searchPredicate = mapToPredicate(root, query, criteriaBuilder, joinMap, searchCriteriaLogicalOperatorMap);
        Predicate sortPredicate = mapToPredicate(root, query, criteriaBuilder, joinMap, sortCriteriaLogicalOperatorMap);
        Predicate havingPredicate = buildHavingPredicate();
        if (havingPredicate != null) {
            query.having(havingPredicate);
        }
        return criteriaBuilder.and(searchPredicate, sortPredicate);
    }

    @FunctionalInterface
    private interface SearchCriteriaPredicateProvider {
        Predicate apply(
                Path<Object> realPath,
                Class<?> javaType,
                Root<?> root,
                CriteriaQuery<?> criteriaQuery,
                CriteriaBuilder criteriaBuilder,
                SearchCriteria searchCriteria,
                Map<String, Join<?, ?>> joinMap
        );
    }

    @FunctionalInterface
    public interface SortCriteriaOrderProvider {
        void apply(
                Path<Object> realPath,
                Class<?> javaType,
                Root<?> root,
                CriteriaQuery<?> criteriaQuery,
                CriteriaBuilder criteriaBuilder,
                SortCriteria sortCriteria,
                Map<String, Join<?, ?>> joinMap
        );
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HavingCondition {
        private Predicate predicate;
        private SpecificationUtils.LogicalOperator logicalOperator;
    }
}