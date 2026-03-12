package com.product.hms.repository;

import com.product.hms.entity.StaffEntity;
import com.product.hms.enums.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long> {

    Optional<StaffEntity> findByUserEntityId(Long userId);

    List<StaffEntity> findAllByOrderByIdAsc();

    /**
     * Find all active staff members by department
     */
    List<StaffEntity> findByDepartmentAndIsActiveTrue(Department department);

    @Query("SELECT s FROM StaffEntity s WHERE s.userEntity.email = :email AND s.isActive = true")
    Optional<StaffEntity> findByUserEntityEmail(@Param("email") String email);
}
