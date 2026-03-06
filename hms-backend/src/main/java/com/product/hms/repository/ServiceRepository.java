package com.product.hms.repository;

import com.product.hms.entity.ServiceEntity;
import com.product.hms.enums.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {


    Page<ServiceEntity> findByServiceCategory(ServiceCategory serviceCategory, Pageable pageable);
}
