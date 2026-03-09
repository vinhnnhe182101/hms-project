package com.product.hms.repository;

import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    /**
     * Tìm kiếm khách hàng theo số chứng minh nhân dân (identity card number).
     *
     * @param identityCard số chứng minh nhân dân của khách hàng
     * @return Optional chứa CustomerEntity nếu tìm thấy, hoặc Optional.empty() nếu không tìm thấy
     */
    Optional<CustomerEntity> findByIdentityCard(String identityCard);

    Optional<CustomerEntity> findByUserEntity(UserEntity userEntity);

    Optional<CustomerEntity> findByEmail(String email);
}

