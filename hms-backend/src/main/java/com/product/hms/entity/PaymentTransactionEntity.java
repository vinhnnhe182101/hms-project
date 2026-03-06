package com.product.hms.entity;

import com.product.hms.enums.PaymentMethod;
import com.product.hms.enums.PaymentTransactionStatus;
import com.product.hms.enums.PaymentTransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "payment_transaction", schema = "hms_db")
public class PaymentTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "folio_id", nullable = false)
    private FolioEntity folioEntity;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod;

    @ColumnDefault("0.00")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private PaymentTransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PaymentTransactionStatus status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "handled_by")
    private StaffEntity handledByEntity;

    @OneToMany(mappedBy = "paymentTransactionEntity")
    private List<PaymentAllocationEntity> paymentAllocationEntities = new ArrayList<>();

    @OneToMany(mappedBy = "paymentTransactionEntity")
    private List<RefundRequestEntity> refundRequestEntities = new ArrayList<>();

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

}