package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "damage_report", schema = "hms_db")
public class DamageReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity roomEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reported_by_staff_id", nullable = false)
    private StaffEntity reportedByStaffEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "reservation_id")
    private ReservationEntity reservationEntity;


    @ColumnDefault("1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ColumnDefault("0.00")
    @Column(name = "penalty_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}