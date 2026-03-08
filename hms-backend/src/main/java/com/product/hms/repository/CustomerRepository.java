package com.product.hms.repository;

import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    /**
     * Find customer by identity card number
     *
     * @param identityCard the identity card number
     * @return Optional containing the customer if found
     */
    Optional<CustomerEntity> findByIdentityCard(String identityCard);
    Optional<CustomerEntity> findByUserEntity(UserEntity userEntity);
    Optional<CustomerEntity> findByEmail(String email);
}

