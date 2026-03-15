package com.product.hms.repository;

import com.product.hms.entity.CustomerEntity;
import com.product.hms.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>, JpaSpecificationExecutor<CustomerEntity> {

    /**
     * Tìm kiếm khách hàng theo số chứng minh nhân dân (identity card number).
     *
     * @param identityCard số chứng minh nhân dân của khách hàng
     * @return Optional chứa CustomerEntity nếu tìm thấy, hoặc Optional.empty() nếu không tìm thấy
     */
    Optional<CustomerEntity> findByIdentityCard(String identityCard);

    Optional<CustomerEntity> findByPhoneNumber(String phoneNumber);

    Optional<CustomerEntity> findByUserEntity(UserEntity userEntity);

    Optional<CustomerEntity> findByEmail(String email);

    /**
     * Đếm số lượng khách hàng đang hoạt động (active) để hiển thị thống kê trên bảng điều khiển (dashboard).
     *
     * @return số lượng khách hàng đang hoạt động
     */
    long countByIsActiveTrue();
}
