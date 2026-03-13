package com.product.hms.repository.specification;

import com.product.hms.entity.StaffEntity;
import com.product.hms.entity.UserEntity;
import com.product.hms.enums.Department;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class StaffSpecification {

    public static Specification<StaffEntity> build(
            String name,
            String email,
            String phoneNumber,
            String department,
            String status,
            Boolean isActive
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (name != null && !name.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%"));
            }

            if (email != null && !email.isBlank()) {
                Join<StaffEntity, UserEntity> userJoin = root.join("userEntity", JoinType.LEFT);
                predicate = cb.and(predicate, cb.like(cb.lower(userJoin.get("email")), "%" + email.toLowerCase() + "%"));
            }

            if (phoneNumber != null && !phoneNumber.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("phoneNumber")), "%" + phoneNumber.toLowerCase() + "%"));
            }

            if (department != null && !department.isBlank()) {
                try {
                    Department dep = Department.valueOf(department);
                    predicate = cb.and(predicate, cb.equal(root.get("department"), dep));
                } catch (IllegalArgumentException ignored) {
                    // If invalid enum provided, no department filter will be applied
                }
            }

            if (status != null && !status.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }

            if (isActive != null) {
                predicate = cb.and(predicate, cb.equal(root.get("isActive"), isActive));
            }

            return predicate;
        };
    }
}
