package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

/**
 * Entity đại diện cho tài khoản người dùng hệ thống.
 *
 * <p>Các thuộc tính chính:</p>
 * <ul>
 *   <li>{@link #id} - ID tài khoản</li>
 *   <li>{@link #email} - Email đăng nhập</li>
 *   <li>{@link #password} - Mật khẩu</li>
 *   <li>{@link #role} - Vai trò</li>
 *   <li>{@link #provider} - Nhà cung cấp xác thực</li>
 *   <li>{@link #providerId} - ID nhà cung cấp</li>
 *   <li>{@link #isActive} - Tài khoản còn hiệu lực</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "user", schema = "hms_db")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @ColumnDefault("'local'")
    @Lob
    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CustomerEntity customerEntity;

    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private StaffEntity staffEntity;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}