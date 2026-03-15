package com.product.hms.repository;

import com.product.hms.entity.RoomTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomTypeEntity, Long> {
    boolean existsByTypeNameIgnoreCase(String typeName);

    boolean existsByTypeNameIgnoreCaseAndIdNot(String typeName, Long id);
}
