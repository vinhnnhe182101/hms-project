package com.product.hms.repository;

import com.product.hms.entity.RoomClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomClassRepository extends JpaRepository<RoomClassEntity, Long> {
}

