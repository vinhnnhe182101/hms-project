package com.product.hms.repository;

import com.product.hms.entity.ServiceBookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBookingEntity, Long> {
}
