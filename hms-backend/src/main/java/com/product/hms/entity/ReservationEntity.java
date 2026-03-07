package com.product.hms.entity;

import com.product.hms.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "reservation", schema = "hms_db")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customerEntity;

    @Column(name = "expected_check_in", nullable = false)
    private Timestamp expectedCheckIn;

    @Column(name = "expected_check_out", nullable = false)
    private Timestamp expectedCheckOut;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ReservationStatus status;

    @ColumnDefault("0.00")
    @Column(name = "total_deposit", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDeposit;


    @ColumnDefault("1")
    @Column(name = "number_of_members", nullable = false)
    private Integer numberOfMembers;

    @Lob
    @Column(name = "note")
    private String note;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "reservationEntity")
    private List<DamageReportEntity> damageReportEntities = new ArrayList<>();

    @OneToOne
    private FolioEntity folioEntity;

    @OneToMany(mappedBy = "reservationEntity")
    private List<RatingEntity> ratingEntities = new ArrayList<>();

    @OneToMany(mappedBy = "reservationEntity")
    private List<ReservationRoomAllocationEntity> reservationRoomAllocationEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}