package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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
    @Column(name = "provider", nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @OneToOne
    private CustomerEntity customerEntity;

    @OneToOne
    private StaffEntity staffEntity;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}