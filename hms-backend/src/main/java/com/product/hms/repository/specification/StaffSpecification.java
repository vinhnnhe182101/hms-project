package com.product.hms.repository.specification;

import com.product.hms.entity.StaffEntity;
import com.product.hms.entity.UserEntity;
import com.product.hms.enums.Department;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.Locale;

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

            // ĐÃ SỬA LỖI LỌC PHÒNG BAN TẠI ĐÂY
            if (department != null && !department.isBlank()) {
                try {
                    // Ép chuỗi truyền vào thành IN HOA để luôn khớp với Enum
                    Department depEnum = Department.valueOf(department.toUpperCase(Locale.ROOT));
                    predicate = cb.and(predicate, cb.equal(root.get("department"), depEnum));
                } catch (IllegalArgumentException e) {
                    // Nếu Frontend truyền lên 1 department sai (vd: "ABC"), ta cho Predicate trả về FALSE (1=0)
                    // Như vậy API sẽ trả về danh sách rỗng, thay vì trả về toàn bộ nhân viên như cũ.
                    predicate = cb.and(predicate, cb.disjunction());
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