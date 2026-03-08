package com.product.hms.repository;

import com.product.hms.entity.ShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<ShiftEntity, Long> {

    List<ShiftEntity> findByIsActiveTrue();
}
