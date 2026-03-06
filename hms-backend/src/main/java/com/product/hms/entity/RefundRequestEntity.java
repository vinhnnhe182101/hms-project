package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "refund_request", schema = "hms_db")
public class RefundRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "payment_transaction_id", nullable = false)
    private PaymentTransactionEntity paymentTransactionEntity;

    @ColumnDefault("0.00")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Lob
    @Column(name = "reason")
    private String reason;

    @Lob
    @Column(name = "reject_reason")
    private String rejectReason;

    @ColumnDefault("'PENDING'")
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private StaffEntity requestedByEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "approved_by")
    private StaffEntity approvedByEntity;

    @ColumnDefault("CURRENT_TIMESTAMP(6)")
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP(6)")
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}