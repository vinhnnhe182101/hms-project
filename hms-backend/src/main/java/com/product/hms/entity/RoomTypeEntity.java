package com.product.hms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "room_types", schema = "hms_db")
public class RoomTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;

    @Column(name = "standard_occupancy", nullable = false)
    private Integer standardOccupancy;

    @Column(name = "max_occupancy", nullable = false)
    private Integer maxOccupancy;

    @Column(name = "base_rate_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseRatePerNight;
}
