package com.product.hms.repository.specification;

import com.product.hms.entity.PaymentTransactionEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.Objects;

public class PaymentTransactionSpecification {

    public static Specification<PaymentTransactionEntity> byFilter(
            String code,
            String paymentMethod,
            String type,
            String status,
            Long folioId
    ) {
        return (root, query, cb) -> {
            // SỬ DỤNG PREDICATE: Khai báo rõ ràng kiểu thay vì dùng 'var'
            Predicate predicates = cb.conjunction();

            if (code != null && !code.isBlank()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            }

            if (paymentMethod != null && !paymentMethod.isBlank()) {
                predicates = cb.and(predicates, cb.equal(root.get("paymentMethod"), paymentMethod));
            }

            if (type != null && !type.isBlank()) {
                predicates = cb.and(predicates, cb.equal(root.get("type"), type));
            }

            if (status != null && !status.isBlank()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }

            if (Objects.nonNull(folioId)) {
                Join<Object, Object> folioJoin = root.join("folioEntity", JoinType.INNER);
                predicates = cb.and(predicates, cb.equal(folioJoin.get("id"), folioId));
            }

            return predicates;
        };
    }
}