package com.product.hms.repository.specification;

import com.product.hms.entity.CustomerEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class CustomerSpecification {

    public static Specification<CustomerEntity> build(String email, Boolean isActive) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (email != null && !email.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            if (isActive != null) {
                predicate = cb.and(predicate, cb.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };
    }
}
