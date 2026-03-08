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
@Table(name = "room_class", schema = "hms_db")
public class RoomClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ColumnDefault("0.00")
    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;


    @ColumnDefault("1")
    @Column(name = "standard_capacity", nullable = false)
    private Integer standardCapacity;


    @ColumnDefault("1")
    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @ColumnDefault("0.00")
    @Column(name = "extra_person_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal extraPersonFee;

    @OneToMany(mappedBy = "roomClassEntity")
    private List<ReservationRoomEntity> reservationRoomAllocationEntities = new ArrayList<>();

    @OneToMany(mappedBy = "roomClassEntity")
    private List<RoomEntity> roomEntities = new ArrayList<>();

    @OneToMany(mappedBy = "roomClassEntity")
    private List<RoomImgEntity> roomImgEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}