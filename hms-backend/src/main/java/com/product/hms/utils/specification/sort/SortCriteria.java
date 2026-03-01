package com.product.hms.utils.specification.sort;

import jakarta.persistence.criteria.JoinType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SortCriteria {
    private String fieldName;
    private AggregationFunction aggregationFunction;
    private SortDirection sortDirection;
    private JoinType joinType;

    @Getter
    @AllArgsConstructor
    public enum AggregationFunction {
        NONE("Không có"),
        AVG("Trung bình"),
        SUM("Tổng"),
        COUNT("Đếm"),
        MAX("Lớn nhất"),
        MIN("Nhỏ nhất");

        private final String function;
    }

    @Getter
    @AllArgsConstructor
    public enum SortDirection {
        ASC("Tăng dần"),
        DESC("Giảm dần");

        private final String direction;
    }
}
