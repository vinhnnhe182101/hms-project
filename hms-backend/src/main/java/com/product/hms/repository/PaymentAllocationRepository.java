package com.product.hms.repository;

import com.product.hms.entity.PaymentAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocationEntity, Long> {
}

