package com.product.hms.entity;

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

    @Lob
    @Column(name = "service_category", nullable = false)
    private String serviceCategoryEntity;

    @ColumnDefault("0.00")
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "serviceEntity")
    private List<ServiceBookingEntity> serviceBookingEntities = new ArrayList<>();


}