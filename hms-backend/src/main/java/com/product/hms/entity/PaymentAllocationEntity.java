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
@Table(name = "payment_allocation", schema = "hms_db")
public class PaymentAllocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "payment_transaction_id", nullable = false)
    private PaymentTransactionEntity paymentTransactionEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "folio_item_id", nullable = false)
    private FolioItemEntity folioItemEntity;

    @ColumnDefault("0.00")
    @Column(name = "amount_applied", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountApplied;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


}