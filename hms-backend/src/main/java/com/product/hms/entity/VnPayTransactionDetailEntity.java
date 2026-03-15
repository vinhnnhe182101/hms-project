package com.product.hms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "vnpay_transaction_detail", schema = "hms_db")
public class VnPayTransactionDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Quan hệ 1-1 trỏ về PaymentTransactionEntity
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "payment_transaction_id", nullable = false, unique = true)
    private PaymentTransactionEntity paymentTransactionEntity;

    @Column(name = "vnp_txn_ref", nullable = false, length = 50)
    private String vnpTxnRef;

    @Column(name = "vnp_transaction_no", length = 50)
    private String vnpTransactionNo;

    @Column(name = "vnp_bank_code", length = 50)
    private String vnpBankCode;

    @Column(name = "vnp_pay_date", length = 14)
    private String vnpPayDate;

    @Lob
    @Column(name = "raw_response")
    private String rawResponse;

    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
