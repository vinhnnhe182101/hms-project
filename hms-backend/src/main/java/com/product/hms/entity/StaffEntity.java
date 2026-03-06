package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "staff", schema = "hms_db")
public class StaffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @OneToMany(mappedBy = "staffEntity")
    private List<AssetHandoverEntity> assetHandoverEntities = new ArrayList<>();

    @OneToMany(mappedBy = "reportedByStaffEntity")
    private List<DamageReportEntity> damageReportEntities = new ArrayList<>();

    @OneToMany(mappedBy = "assigneeEntity")
    private List<HousekeepingTaskEntity> housekeepingTaskEntities = new ArrayList<>();

    @OneToMany(mappedBy = "handledByEntity")
    private List<PaymentTransactionEntity> paymentTransactionEntities = new ArrayList<>();

    @OneToMany(mappedBy = "requestedByEntity")
    private List<RefundRequestEntity> refundRequest1Entities = new ArrayList<>();

    @OneToMany(mappedBy = "approvedByEntity")
    private List<RefundRequestEntity> refundRequest2Entities = new ArrayList<>();

    @OneToMany(mappedBy = "staffEntity")
    private List<WorkScheduleEntity> workScheduleEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}