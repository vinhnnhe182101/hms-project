package com.product.hms.entity;

import com.product.hms.enums.ServiceCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "service", schema = "hms_db")
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "service_category", nullable = false, length = 50)
    private ServiceCategory serviceCategory;

    @ColumnDefault("0.00")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "serviceEntity")
    private List<ServiceBookingEntity> serviceBookingEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}