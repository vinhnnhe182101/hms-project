package com.product.hms.repository;

import com.product.hms.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<StaffEntity, Long> {

    Optional<StaffEntity> findByUserEntityId(Long userId);

    List<StaffEntity> findAllByOrderByIdAsc();
}
