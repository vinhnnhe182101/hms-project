package com.product.hms.repository;

import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByUserEntity(UserEntity userEntity);
    Optional<CustomerEntity> findByEmail(String email);
}